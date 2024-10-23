package mo.core.ui.dockables;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.location.CBaseLocation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.util.xml.XElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DockablesTreeRecreator {

    private static final Logger LOGGER = Logger.getLogger(DockablesRegistry.class.getName());

    private CControl control;
    private HashMap<String, List<LocationNode>> treesByRoot;

    private static DockablesTreeRecreator instance;
    
    public static synchronized DockablesTreeRecreator getInstance(CControl c) {
        if (instance == null) {
            instance = new DockablesTreeRecreator(c);
        }
        return instance;
    }
    
    public DockablesTreeRecreator(CControl control) {
        this.control = control;
        treesByRoot = new HashMap<>();
    }

    public void addDockable(DockableElement element, XElement locationProperty) {

        XElement[] nodes = locationProperty.getElements("node");
        int lastNodeIndex = nodes.length - 1;
        XElement lastXNode = nodes[lastNodeIndex];

        String locationStr = lastXNode.getAttribute("location").getString();

        LocationNode last = new LocationNode();
        last.docks.add(element);
        last.id = lastXNode.getAttribute("id").getString();
        last.location = Location.fromString(locationStr);
        last.size = lastXNode.getAttribute("size").getFloat();

        LocationNode lastVisited = last;

        for (int j = lastNodeIndex; j > -1; j--) {

            LocationNode n = new LocationNode();
            n.id = nodes[j].getAttribute("id").getString();

            if (j > 0) {
                n.location = Location.fromString(nodes[j - 1]
                        .getAttribute("location").getString());

                n.size = nodes[j - 1].getAttribute("size").getFloat();
            }

            Location childLocation = lastVisited.location;

            if (null != childLocation) {
                switch (childLocation) {
                    case TOP:
                        n.childrenOrientation = ChildrenOrientation.VERTICAL;
                        n.firstChild = lastVisited;
                        break;
                    case BOTTOM:
                        n.childrenOrientation = ChildrenOrientation.VERTICAL;
                        n.secondChild = lastVisited;
                        break;
                    case LEFT:
                        n.childrenOrientation = ChildrenOrientation.HORIZONTAL;
                        n.firstChild = lastVisited;
                        break;
                    case RIGHT:
                        n.childrenOrientation = ChildrenOrientation.HORIZONTAL;
                        n.secondChild = lastVisited;
                        break;
                    default:
                        break;
                }
            }
            lastVisited = n;
        }

        //lastVisited.preOrderPrint();
        if (!treesByRoot.containsKey(lastVisited.id)) {
            treesByRoot.put(lastVisited.id, new ArrayList<>());
        }

        treesByRoot.get(lastVisited.id).add(lastVisited);
    }

    public List<LocationNode> joinTrees() {
        ArrayList<LocationNode> trees = new ArrayList<>();

        for (String id : treesByRoot.keySet()) {
            LocationNode tree = null;
            for (LocationNode node : treesByRoot.get(id)) {
                if (tree == null) {
                    tree = node;
                } else {
                    LocationNode currentMain = tree;
                    LocationNode currentNew = node;
                    boolean added = false;
                    while (!added) {
                        if (currentNew.firstChild != null) {
                            currentNew = currentNew.firstChild;
                            if (currentMain.firstChild != null) {
                                currentMain = currentMain.firstChild;
                            } else {
                                currentMain.firstChild = currentNew;
                                added = true;
                            }
                        } else if (currentNew.secondChild != null) {
                            currentNew = currentNew.secondChild;
                            if (currentMain.secondChild != null) {
                                currentMain = currentMain.secondChild;
                            } else {
                                currentMain.secondChild = currentNew;
                                added = true;
                            }
                        } else {
                            if (!currentMain.docks.contains(currentNew.docks.get(0))) {
                                currentMain.docks.add(currentNew.docks.get(0));
                            }
                            added = true;
                        }
                    }
                }
            }
            trees.add(tree);
        }
        return trees;
    }

    public void createTrees(List<LocationNode> trees) {
        for (LocationNode tree : trees) {
            CGrid g = createGrid(control, tree);
            control.getContentArea().deploy(g);
        }
    }

    private CGrid createGrid(CControl control, LocationNode root) {
        CGrid grid = new CGrid(control);
        addDockableFromTree(grid, root, 0, 0, 1, 1);
        return grid;
    }

    private void addDockableFromTree(CGrid grid, LocationNode node, float x, float y, float w, float h) {
        if (node.docks.size() > 0) {
            DockableElement[] arr = new DockableElement[node.docks.size()];
            node.docks.toArray(arr);
            grid.add(x, y, w, h, arr);
        } else {
            float fw = w, fh = h;
            if (node.firstChild != null) {

                if (node.childrenOrientation.equals(ChildrenOrientation.HORIZONTAL)) {
                    if (node.secondChild != null) {
                        fw *= node.firstChild.size;
                    }
                } else if (node.secondChild != null) {
                    fh *= node.firstChild.size;
                }

                addDockableFromTree(grid, node.firstChild, x, y, fw, fh);
            }

            if (node.secondChild != null) {

                if (node.childrenOrientation.equals(ChildrenOrientation.HORIZONTAL)) {
                    if (node.firstChild != null) {
                        x += node.firstChild.size * w;
                        w *= node.secondChild.size;
                    }
                } else if (node.firstChild != null) {
                    y += node.firstChild.size * h;
                    h *= node.secondChild.size;
                }

                addDockableFromTree(grid, node.secondChild, x, y, w, h);
            }
        }
    }

    private CLocation getLocation(CDockable d) {
        if (d.getBaseLocation() == null) {
            if (d.getAutoBaseLocation(false) != null) {
                return d.getAutoBaseLocation(false);
            } else if (d.getAutoBaseLocation(true) != null) {
                return d.getAutoBaseLocation(true);
            } else {
                return new CBaseLocation();
            }
        } else {
            return d.getBaseLocation();
        }
    }

    public enum ChildrenOrientation {
        HORIZONTAL, VERTICAL
    }

    public enum Location {
        TOP("TOP"), BOTTOM("BOTTOM"), LEFT("LEFT"), RIGHT("RIGHT");

        private final String val;

        private Location(String s) {
            this.val = s;
        }

        public static Location fromString(String s) {
            s = s.toUpperCase();
            for (Location value : values()) {
                if (value.toString().equals(s)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Unrecognised string for location");
        }
    }

    public static List<DockableElement> findDockablesInControlWithBounds(CControl control, int x, int y, int w, int h) {
        ArrayList<DockableElement> result = new ArrayList<>();
        for (int i = 0; i < control.getCDockableCount(); i++) {
            boolean xtrue = false, ytrue = false, wtrue = false, htrue = false;
            DockableElement d = (DockableElement) control.getCDockable(i);

            if (d.getLocation().findProperty() == null) {
                continue;
            }

            DockableProperty p = d.getLocation().findProperty();

            XElement storage = new XElement("storage");

            p.store(storage);
            for (int j = 0; j < storage.getElementCount(); j++) {
                XElement current = storage.getElement(j);
                try {
                    if (current.getName().equals("x")
                            && current.getInt() == x) {
                        xtrue = true;
                    } else if (current.getName().equals("y")
                            && current.getInt() == y) {
                        ytrue = true;
                    } else if (current.getName().equals("width")
                            && current.getInt() == w) {
                        wtrue = true;
                    } else if (current.getName().equals("height")
                            && current.getInt() == h) {
                        htrue = true;
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }

            if (xtrue && ytrue && wtrue && htrue) {
                result.add(d);
            }
        }
        return result;
    }

    public class LocationNode {

        public String id;
        public float size;
        public Location location;
        public List<DockableElement> docks = null;
        public String type;

        public ChildrenOrientation childrenOrientation = null;
        public LocationNode firstChild = null; //left or top
        public LocationNode secondChild = null;//right or bottom

        LocationNode() {
            docks = new ArrayList<>();
        }

        LocationNode(float s, Location l) {
            docks = new ArrayList<>();
            size = s;
            location = l;
        }

        LocationNode(ChildrenOrientation o, Location l, float s) {
            childrenOrientation = o;
            location = l;
            size = s;
        }

        LocationNode(ChildrenOrientation o, float s, Location l) {
            childrenOrientation = o;
            location = l;
            size = s;
        }

        public void preOrderPrint() {
            preOrderPrint("");
        }

        private void preOrderPrint(String indentation) {

            if (this.firstChild != null) {
                this.firstChild.preOrderPrint(indentation + "  ");
            }

            if (this.secondChild != null) {
                this.secondChild.preOrderPrint(indentation + "  ");
            }

        }

        @Override
        public String toString() {
            return "[ " + id + " " + location + " " + size + " " + childrenOrientation + " "
                    + ((firstChild == null && secondChild == null) ? "leaf" : "node")
                    + " " + docks + "]";
        }
    }
}
