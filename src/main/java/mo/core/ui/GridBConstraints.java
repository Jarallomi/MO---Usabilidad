package mo.core.ui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GridBConstraints extends GridBagConstraints {
    
    public GridBConstraints clear(){
        this.anchor = GridBagConstraints.CENTER;
        this.fill = GridBagConstraints.NONE;
        this.gridheight = 1;
        this.gridwidth = 1;
        this.gridx = GridBagConstraints.RELATIVE;
        this.gridy = GridBagConstraints.RELATIVE;
        this.insets = new Insets(0, 0, 0, 0);
        this.ipadx = 0;
        this.ipady = 0;
        this.weightx = 0;
        this.weighty = 0;
        return this;
    }
    
    public GridBConstraints a(int anchor){
        this.anchor = anchor;
        return this;
    }
    public GridBConstraints f(int fill){
        this.fill = fill;
        return this;
    }
    
    public GridBConstraints gh(int gridheight){
        this.gridheight = gridheight;
        return this;
    }
    
    public GridBConstraints gw(int gridwidth){
        this.gridwidth = gridwidth;
        return this;
    }
    
    public GridBConstraints gx(int gridx){
        this.gridx = gridx;
        return this;
    }
    
    public GridBConstraints gy(int gridy){
        this.gridy = gridy;
        return this;
    }
    
    public GridBConstraints i(Insets insets){
        this.insets = insets;
        return this;
    }
    
    public GridBConstraints ix(int ipadx){
        this.ipadx = ipadx;
        return this;
    }
    
    public GridBConstraints iy(int ipady){
        this.ipady = ipady;
        return this;
    }
    
    public GridBConstraints wx(double weightx){
        this.weightx = weightx;
        return this;
    }
    
    public GridBConstraints wy(double weighty){
        this.weighty = weighty;
        return this;
    }

    @Override
    public String toString() {
        String s = "[anchor:"+anchor+" fill:"+fill+" x:"+gridx+" y:"+gridy+
                " w:"+gridwidth+" h:"+gridheight+" insets:"+insets+" ipadx:"+ipadx+
                " ipady:"+ipady+" wx:"+weightx+" wy:"+weighty+"]";
        return s;
    }
}
