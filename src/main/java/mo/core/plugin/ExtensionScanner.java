package mo.core.plugin;

import java.util.logging.Level;
import java.util.logging.Logger;
import static mo.core.plugin.VersionUtils.semverize;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class ExtensionScanner extends ClassVisitor {

    private static final
            String EXTENSION_DESC = "Lmo/core/plugin/Extension;";
    
    private static final
            String EXTENDS_DESC = "Lmo/core/plugin/Extends;";
    
    private static final
            String EXTENSION_POINT_DESC = "Lmo/core/plugin/ExtensionPoint;";
    
    private Plugin plugin = new Plugin();
    
    private ExtPoint extPoint = new ExtPoint();
    
    private boolean isExtension = false;
    
    private boolean isExtensionPoint = false;
    
    private ClassLoader cl;
    
    private final static Logger LOGGER = 
            Logger.getLogger(ExtensionScanner.class.getName()); 
    
    public ExtensionScanner(int i) {    
        super(i);
    }
    
    public void setClassLoader(ClassLoader cl){
        this.cl = cl;
    }
    
    @Override
    public void visit(int version, int access, String name, 
            String signature, String superName, String[] interfaces) {
        plugin.setId(name.replace("/", "."));
        extPoint.setId(name.replace("/", "."));
        super.visit(version, access, name, signature, superName, interfaces);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {

        if (desc.compareTo(EXTENSION_DESC) == 0) {

            isExtension = true;
            return new AnnotationVisitor(Opcodes.ASM5, 
                    super.visitAnnotation(desc, visible)) {
                
                @Override
                public void visit(String nam, Object value) {

                    switch (nam) {
                        case "id":
                            plugin.setId((String) value);
                            break;
                        case "version":
                            plugin.setVersion((String) value);
                            break;
                        case "name":
                            plugin.setName((String) value);
                            break;
                        case "description":
                            plugin.setDescription((String) value);
                            break;
                        default:
                            break;
                    }
                    super.visit(nam, value);
                }
                
                @Override
                public AnnotationVisitor visitArray(String string) {
                    
                    return new AnnotationVisitor(Opcodes.ASM5, super.visitArray(string)) {
                        
                        @Override
                        public AnnotationVisitor visitAnnotation(String name, String desc) {
                            Dependency d = new Dependency();

                            if (desc.compareTo(EXTENDS_DESC) == 0) {
                                return new AnnotationVisitor(Opcodes.ASM5, super.visitAnnotation(name, desc)) {
                                    @Override
                                    public void visit(String name, Object value) {

                                        switch (name) {
                                            case "extensionPointId":
                                                d.setId((String) value);

                                                break;
                                            case "extensionPointVersion":
                                                d.setVersion((String) value);

                                                
                                                break;
                                            default:
                                                break;
                                        }

                                        super.visit(name, value);
                                    }

                                    @Override
                                    public void visitEnd() {

                                        plugin.addDependency(d);
                                        super.visitEnd();
                                    }
                                };
                            } else {
                                return super.visitAnnotation(name, desc);
                            }
                            
                        }
                        
                    };
                }
            };
        } else if (desc.compareTo(EXTENSION_POINT_DESC) == 0) {
            isExtensionPoint = true;
            return new AnnotationVisitor(Opcodes.ASM5, 
                    super.visitAnnotation(desc, visible)) {
                
                @Override
                public void visit(String nam, Object value) {

                    switch (nam) {
                        case "id":
                            extPoint.setId((String) value);
                            break;
                        case "version":
                            extPoint.setVersion((String) value);

                            break;
                        case "name":
                            extPoint.setName((String) value);
                            break;
                        case "description":
                            extPoint.setDescription((String) value);
                            break;
                        default:
                            break;
                    }
                    super.visit(nam, value);
                }
            };
                    
        } else {
            return super.visitAnnotation(desc, visible);
        }
    }

    @Override
    public void visitEnd() {
        if (!isExtension && !isExtensionPoint) {
            this.plugin = null;
            this.extPoint = null;
        } else if (isExtensionPoint) {
            this.plugin = null;
            if (extPoint.getVersion() == null)
                extPoint.setVersion("0.0.0");
            
            if (extPoint.getName() == null) {
                String id = extPoint.getId();
                if (id.contains("."))
                    extPoint.setName(id.substring(id.lastIndexOf('.') + 1));
                else
                    extPoint.setName(id);
            }
            
            extPoint.setVersion(semverize(extPoint.getVersion()));
 
        } else if (isExtension) {
            this.extPoint = null;
            try {
                Class c = Class.forName(plugin.getId());
                plugin.setClazz(c);
            } catch (NoClassDefFoundError | ClassNotFoundException | IllegalAccessError ex) {
                try {
                    plugin.setClazz(cl.loadClass(plugin.getId()));
                } catch (ClassNotFoundException ex1) {
                    LOGGER.log(Level.SEVERE, null, ex1);
                }
            }
            
            if (plugin.getVersion() == null)
                plugin.setVersion("0.0.0");
            
            if (plugin.getName() == null) {
                String id = plugin.getId();
                if (id.contains("."))
                    plugin.setName(id.substring(id.lastIndexOf('.') + 1));
                else
                    plugin.setName(id);
            }
            
            plugin.setVersion(semverize(plugin.getVersion()));
            
        }
        super.visitEnd();
    }

    public Plugin getPlugin(){
        return this.plugin;
    }
    
    public ExtPoint getExtPoint() {
        return this.extPoint;
    }   
}
