package mo.organization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileDescription {

    private File descriptionFile;
    private File file;
    private String creator;

    private String captureFile;
    private String configuration;

    public FileDescription(File file, String creator) {
        this.file = file;
        this.creator = creator;
        String name = getNameWithoutExtension(file);
        descriptionFile = new File(file.getParentFile(), name + ".desc");

        try (BufferedWriter w = new BufferedWriter(new FileWriter(descriptionFile))) {
            w.write("file=" + relativePath(descriptionFile, this.file) + "\n");
            w.write("creator=" + this.creator + "\n");
        } catch (IOException ex) {
            Logger.getLogger(FileDescription.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public FileDescription(File file, String creator, String captureFile, String configuration) {
        this.file = file;
        this.creator = creator;
        this.captureFile = captureFile;
        this.configuration = configuration;

        String name = getNameWithoutExtension(file);
        descriptionFile = new File(file.getParentFile(), name + ".desc");

        try (BufferedWriter w = new BufferedWriter(new FileWriter(descriptionFile))) {
            w.write("file=" + relativePath(descriptionFile, this.file) + "\n");
            w.write("creator=" + this.creator + "\n");
            w.write("captureFile=" + this.captureFile + "\n");
            w.write("configuration=" + this.configuration);

        } catch (IOException ex) {
            Logger.getLogger(FileDescription.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getNameWithoutExtension(File file) {
        String s = file.getName();
        if (s.contains(".")) {
            s = s.substring(0, s.lastIndexOf("."));
        }
        return s;
    }

    private String relativePath(File file1, File file2) {
        Path f1 = file1.toPath();
        Path f2 = file2.toPath();
        Path relative = f1.relativize(f2);
        return relative.toString();
    }

    public void deleteFileDescription() {
        descriptionFile.delete();
    }

    public File getDescriptionFile() {
        return descriptionFile;
    }

    public void setDescriptionFile(File descriptionFile) {
        this.descriptionFile = descriptionFile;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

}
