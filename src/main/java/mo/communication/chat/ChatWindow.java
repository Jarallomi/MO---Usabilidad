package mo.communication.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
import mo.communication.Command;
import mo.communication.CommunicationConfiguration;
import mo.communication.ConnectionListener;
import mo.communication.ConnectionSender;
import mo.communication.PetitionResponse;
import mo.communication.ServerConnection;

public class ChatWindow implements CommunicationConfiguration, ConnectionListener, ConnectionSender{

    private JPanel chatPanel;
    private JFrame windowFrame;
    private JButton sendButton;
    private JTextArea msgArea;
    private String name;
    
    public ChatWindow(String name){
        if(name == null)
            this.name = "Participant";
        else
            this.name = name;
        windowFrame = new JFrame();
        initComponents();
    }
    
    public void initComponents(){
        chatPanel = new JPanel();
        chatPanel.setLayout(new GridBagLayout());
        chatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        
        //VENTANA
        windowFrame.setTitle("Chat Window");
        windowFrame.setSize(360, 600);
        windowFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        windowFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                windowFrame.setExtendedState(JFrame.ICONIFIED);
            }
        });
        windowFrame.setLocationRelativeTo(null);
        
        
        //PANEL PRINCIPAL
        JPanel container = new JPanel();
        container.setLayout(new GridBagLayout());
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
       
        
        // PANEL EN EL QUE SE MUESTRAN LOS MENSAJES
        JPanel msgScrollPanel = new JPanel();
        msgScrollPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        msgScrollPanel.add(chatPanel, gbc);
        VerticalScrollPane vsp = new VerticalScrollPane(msgScrollPanel);
        gbc.weightx = 2.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        //gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        container.add(new JScrollPane(vsp),gbc);
        
        
        //PANEL EN EL CUAL SE ESCRIBEN LOS MENSAJES Y TIENE EL BOTON DE ENVIAR
        JPanel jp = new JPanel(new BorderLayout());
        jp.setPreferredSize(new Dimension(175,100));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 2.0;
        gbc.weighty=0.0;
        jp.setBorder(new EtchedBorder());
        
        
        // PARA ESCRIBIR MENSAJE
        msgArea = new JTextArea();
        //JScrollPane scrollMsgArea = new JScrollPane(msgArea);
        //jp.add(scrollMsgArea,BorderLayout.CENTER);
        msgArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(msgArea);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {  
                //scrollPane.getVerticalScrollBar().setValue( scrollPane.getVerticalScrollBar().getMaximum() );
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
            }
        });
        msgArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    msgArea.setText(null);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    sendButton.doClick();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    msgArea.setText(null);
            }

        });
        jp.add(scrollPane,BorderLayout.CENTER);
        
        //PARA ENVIAR
        sendButton = new JButton("send");
        jp.add(sendButton,BorderLayout.EAST);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        });
        
        container.add(jp,gbc);
        
        // AGREGA EL CONTENEDOR PRINCIPAL A LA VENTANA
        windowFrame.getContentPane().add(container);
        //setVisible(true);
    }
    
    public void sendMsg(){
        if(msgArea.getText() != null && !msgArea.getText().equals("")){
            String msg = msgArea.getText();
            msgArea.setText(null);
            int type = 0;
            
            HashMap<String,Object> map = new HashMap<>();
            map.put("name", name);
            map.put("msg", msg);
            map.put("ip", msg);
            PetitionResponse r = new PetitionResponse(Command.MSG_SERVER_TO_CLIENT,map);
//            boolean sent = false;
//            ArrayList<RemoteClient> inactiveClients = new ArrayList<>();
//            for(RemoteClient rc: ServerConnection.getInstance().getClients()){
//                sent = rc.send(r);
//                if(!sent)
//                    inactiveClients.add(rc);
//                
//            }
//            ServerConnection.getInstance().removeClients(inactiveClients);
//            
//            insertMsg(new ChatEntry("You", msg, type));
            if(listener != null){
                listener.onMessageReceived(this, r);
                System.out.println("Listener de ChatWindow: "+listener.toString());
                insertMsg(new ChatEntry("You", msg, type));
            }
            else{
                insertMsg(new ChatEntry("You", "No connection", 2));
            }
        }
    }

    public void insertMsg(ChatEntry entry) {
        GridBagConstraints gbc = new GridBagConstraints();

            JLabel nameLabel = new JLabel(entry.name);

            BubblePane bubble = new BubblePane(chatPanel, entry.content);

            // Arrange each chat entry based on the user.
            switch (entry.type) {
                case 0:
                    bubble.setBackground(Color.CYAN);
                    gbc.anchor = GridBagConstraints.EAST;
                    break;
                case 1:
                    bubble.setBackground(Color.YELLOW);
                    gbc.anchor = GridBagConstraints.WEST;
                    break;
                default:
                    bubble.setBackground(Color.GRAY);
                    gbc.anchor = GridBagConstraints.EAST;
                    break;
            }

            gbc.insets.set(0, 0, 0, 0);
            gbc.weightx = 1.0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.NONE;
            chatPanel.add(nameLabel, gbc);

            if (gbc.anchor == GridBagConstraints.WEST) {
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets.set(0, 0, 10, 40);
                chatPanel.add(bubble, gbc);
            }
            else {
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets.set(0, 40, 10, 0);
                chatPanel.add(bubble, gbc);
            }
            saveMsg(entry);
        chatPanel.revalidate();
        chatPanel.repaint();
    }
    
    public void setName(String name){
        this.name = name;
    }
    
        
    File parentDir;
    String fileName;
    File storageFolder;
    
    public void setInfo(File parentDir){
        try{
            if(!parentDir.equals(this.parentDir)){
                this.parentDir = parentDir;
                createFileName();
                File path = new File(this.parentDir.getParent()+"\\communication\\");
                if(!path.exists())
                    path.mkdir();
                this.storageFolder = new File(this.parentDir.getParent()+"\\communication\\"+this.fileName);
                this.storageFolder.createNewFile();
            }
        }catch(NullPointerException | IOException ex){}
    }
    
    public void createFileName(){
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");
        String reportDate = df.format(now);
        this.fileName = reportDate + "_CHAT.txt";
    }
    
    public void saveMsg(ChatEntry entry){//HashMap<String,Object> map){
        if(storageFolder != null){
            BufferedWriter output = null;
            try {
                String msg =String.valueOf(System.currentTimeMillis())+"\t"+ entry.name+": "+entry.content+"\n\n";
                
                
                output = new BufferedWriter(new FileWriter(storageFolder, true));
                output.write(msg);
                output.newLine();
                output.flush();
                //output.append(msg);
                output.close();
                System.out.println("GUARDANDO EN "+storageFolder.getAbsolutePath());
                return;
            } catch (Exception ex) {
                try {output.close();} catch (IOException ex1) {}
                Logger.getLogger(ChatWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    

    public static void main(String[] args) throws InterruptedException {

        ChatWindow cw = new ChatWindow(null);
        cw.insertMsg(new ChatEntry("David", "Hey Lori, how are you?", 0));
        Thread.sleep(1000);
        cw.insertMsg(new ChatEntry("Lori", "Hi David, I'm good. What have you been up to?", 1));
        Thread.sleep(1000);
        cw.insertMsg(new ChatEntry("David", "I've been super busy with work.", 0));
        Thread.sleep(1000);
        cw.insertMsg(new ChatEntry("David", "Haven't had much free time to even go out to eat.", 0));

    }

    @Override
    public void showPlayer() {
        windowFrame.setVisible(true);
    }

    @Override
    public void closePlayer() {
        windowFrame.setVisible(false);
    }

    @Override
    public void onMessageReceived(Object obj, PetitionResponse petition) {
        if(petition.getType().equals(Command.MSG_CLIENT_TO_SERVER)){
            ChatEntry entry = new ChatEntry(
                        (String)petition.getHashMap().get("name"),
                        (String)petition.getHashMap().get("msg"),
                        1);
            insertMsg(entry);
        }
    }

    private ConnectionListener listener;
    @Override
    public void subscribeListener(ConnectionListener c) {
        listener = c;
    }

    @Override
    public void unsubscribeListener(ConnectionListener c) {
        listener = null;
    }
}