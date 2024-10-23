package mo.core.plugin.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import mo.core.plugin.IUpdatable;

/**
 * An easy way to add a right click menu with a "Refresh" button.
 * When the button is clicked, the IUpdatable object will be updated.
 * 
 * @author Felo
 */

public class UpdateRightClick extends MouseAdapter{    
    
    class DropDownMenu extends JPopupMenu {
        JMenuItem refresh;
        IUpdatable updatable;
        public DropDownMenu(IUpdatable u){
            this.updatable = u;
            refresh = new JMenuItem("Refresh");
            add(refresh);
            refresh.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    u.update();
                }
            });            
        }
    }
    
    private final IUpdatable updatable;

    public UpdateRightClick(IUpdatable u){
        this.updatable = u;            
    }

    @Override
    public void mousePressed(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    @Override
    public void mouseReleased(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e){
        DropDownMenu menu = new DropDownMenu(updatable);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}

