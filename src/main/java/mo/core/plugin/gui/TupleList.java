package mo.core.plugin.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 *
 * @author felo
 */
public class TupleList extends JPanel {    
        
    private final GridBagConstraints grid;
    
    private static final String EMPTY = "---";
    
    private static final int MAX_ROWS_TABLE = 5;
    
    private final JPanel subContainer;
    
    TupleList(){        

        subContainer = new JPanel();    
        
        subContainer.setLayout(new GridBagLayout());
	grid = new GridBagConstraints();
        
        grid.gridy = 0;
        grid.insets = new Insets(10, 10, 10, 10);

        grid.fill = GridBagConstraints.HORIZONTAL;
        grid.anchor = GridBagConstraints.NORTHWEST;

        add(subContainer, BorderLayout.PAGE_START);

    }
    
    
    private void addTuple(String key, JComponent value){
        
        grid.gridx = 0;
        grid.weightx = 1;        
	subContainer.add(new JLabel(key, SwingConstants.RIGHT), grid);

	grid.gridx = 1;	
        grid.weightx = 25;
	subContainer.add(value, grid);
        
        grid.gridy++;
        
    }
    

    
    public void addTuple(String key, Object value){
        
        JComponent comp = null;        
        
        if(value instanceof JTable){
            // make the table smaller
            JTable table = (JTable) value;
            comp = new JScrollPane(table);        
            Dimension depDimension = table.getPreferredSize();
            
            // set max height
            comp.setPreferredSize(new Dimension(400, table.getRowHeight() * (Math.min(table.getRowCount(), MAX_ROWS_TABLE) + 2)));            
            
        } else if(value instanceof JComponent){
            comp = (JComponent) value;
        } else if(value == null){
            comp = new JLabel(EMPTY);
        } else {
            
            String text = value.toString().length() == 0? EMPTY : value.toString();
            
            comp = new JLabel(text);
        }        
        
        addTuple(key, comp);
    }
    
    public void addScrollText(String key, String value){        

        JTextArea textArea = new JTextArea(5, 20);
        textArea.setOpaque(false);

        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea); 
        textArea.setText(value);
        textArea.setEditable(false);        
        
        addTuple(key, scrollPane); 
        
    }    
    
}
