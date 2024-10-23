package mo.core.plugin.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Future;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import mo.core.MultimodalObserver;
import mo.core.preferences.AppPreferencesWrapper;
import mo.core.preferences.PreferencesManager;
import mo.core.ui.Utils;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.HttpResponseStatus;
import org.asynchttpclient.Response;



class Plugin {
    
    String name, desc, homePage, shortName, repoUser, repoName;
    
    Plugin(String name, String desc, String homePage, String shortName, String repoUser, String repoName){
        this.name = name;
        this.desc = desc;
        this.homePage = homePage;
        this.shortName = shortName;
        this.repoUser = repoUser;
        this.repoName = repoName;
    
    }
    @Override
    public String toString(){
        return "Plugin: " + this.name;
    }
}

class TagNode extends DefaultMutableTreeNode{
    
    String shortName;
    
    TagNode(String shortName){
        super(shortName);
        this.shortName = shortName;
    }
}

class RemotePluginInfo extends JPanel{
    
    Plugin plugin;
    RemotePluginInstaller container;
    
    RemotePluginInfo(Plugin plugin, RemotePluginInstaller container){
        this.plugin = plugin;
        this.container = container;
        
        
        TupleList tuples = new TupleList();
        
        tuples.addTuple("Name", plugin.name);        
        tuples.addTuple("Website", plugin.homePage);
        tuples.addScrollText("Description", plugin.desc);
        
        JButton installBtn = new JButton("Install");
        
        installBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                container.installPlugin(plugin);
            }
        });
        
        tuples.addTuple("", installBtn);
        
        Title pluginTitle = new Title(plugin.name);
        
        JPanel top = new JPanel();
        top.add(pluginTitle);
        
        setLayout(new BorderLayout());
        
        add(top, BorderLayout.NORTH);
        add(tuples, BorderLayout.CENTER);

    }
}



/**
 *
 * @author felo
 */
public final class RemotePluginInstaller extends JPanel {
    
    private final String CHECK_SERVER_BUTTON_LABEL = "Use server";
    
    private final int DELAY_MILLISECONDS = 800;
    
    private final int SEARCH_LIMIT = 10; // Fixed value, the server always gives you 10 per query.
    
    private String currentServer = null;    
    
    private JTextField serverInput = new JTextField("http://localhost:3000", 50);
    private JButton serverCheckButton = new JButton(CHECK_SERVER_BUTTON_LABEL);
    private JLabel serverNotice = new JLabel();    
    private PlaceholderTextField searchInput = new PlaceholderTextField(50);
    
    private JButton changeServer = new JButton("Use different server");
    
    ArrayList<HashMap<String, Object>> pluginsResult = new ArrayList<>();
    
    DefaultMutableTreeNode tagsNode = new DefaultMutableTreeNode("Tags");
    DefaultMutableTreeNode pluginsNode = new DefaultMutableTreeNode("Plugins");
    
    SplitPaneTriple split;
    
    Spinner tagsSpinner;
    Spinner pluginsSpinner;
    
    AppPreferencesWrapper prefs
                    = (AppPreferencesWrapper) PreferencesManager.loadOrCreate(
                            AppPreferencesWrapper.class,
                            new File(MultimodalObserver.APP_PREFERENCES_FILE));
    
    
    private Thread searchDelay;
    
    
    private void setServerNotice(){
        
        if(currentServer == null){
            searchInput.setEnabled(false);
            serverNotice.setText("<html><p style='color: red;'>You haven't selected a server.</p></html>");
        } else {
            searchInput.setEnabled(true);
            serverNotice.setText("<html><p>You are using server <b>" + currentServer + "</b>.</p></html>");
        }                        
    }
    

    
    private void searchByTag(String tagName){
        
        System.out.println("Buscando por tag: " + tagName);
        String pluginsUrl = Utils.cleanServerUrl(currentServer) + "/tags/"+tagName+"/plugins";
        
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        
        split.setPluginSpinner(pluginsSpinner);
                
        asyncHttpClient.prepareGet(pluginsUrl).execute(new AsyncCompletionHandler<Response>(){                
            @Override
            public Response onCompleted(Response r) throws Exception{                        
                String json = r.getResponseBody();     
                
                pluginsSpinner.completeStep();
                showPluginSearchResults(json, tagName);
                return r;
            }
            @Override
            public void onThrowable(Throwable t){ 
                JOptionPane.showMessageDialog(null, "There was a connection error. Did you choose a working server?", "Error", JOptionPane.ERROR_MESSAGE);
            }                
        });
        
    }
    
    
    public void installPlugin(Plugin plugin){
        installPlugin(plugin.name, plugin.shortName, plugin.desc, plugin.homePage, plugin.repoName, plugin.repoUser);
    }
    
    
    private void setDownloadPanel(String repoName, String version, Component backComponent, String downloadUrl, int size, JScrollPane container){
        
        JPanel downloadPanel = new JPanel();
        downloadPanel.setLayout(new BorderLayout());
        
        LogScroll log = new LogScroll();
        
        downloadPanel.add(log, BorderLayout.NORTH);
        
        log.addLine("Beginning download...");
        log.addLine("Downloading from " + downloadUrl);
        log.addLine("Total size: " + size + " bytes");

        
        JPanel content = new JPanel();
        
        JProgressBar progress = new JProgressBar(0, size);
        progress.setValue(0);
        content.add(progress);
        
        String[] fileNameSplit = downloadUrl.split("/");
        String fileName = fileNameSplit[fileNameSplit.length-1];
        
        
        JButton backBtn = new JButton("Cancel");
        
        backBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               container.setViewportView(backComponent);
            }
        });
        
        content.add(backBtn);                

       
        downloadPanel.add(content, BorderLayout.CENTER);
        
        container.setViewportView(downloadPanel);
        
        
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        
        
        AsyncCompletionHandler handler;
        handler = new AsyncCompletionHandler<Response>() {
            
            ByteArrayOutputStream fileBytes = new ByteArrayOutputStream();
            
            
            @Override
            public AsyncHandler.State onStatusReceived(HttpResponseStatus status) throws Exception {
                int statusCode = status.getStatusCode();
                
                if (statusCode >= 500) {
                    return AsyncHandler.State.ABORT;
                }                
                
                return AsyncHandler.State.CONTINUE;
            }

            @Override
            public AsyncHandler.State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {                
                int current = progress.getValue();
                current += bodyPart.getBodyPartBytes().length;
                
                fileBytes.write(bodyPart.getBodyPartBytes(), 0, bodyPart.getBodyPartBytes().length);
                
                progress.setValue(current);
                return AsyncHandler.State.CONTINUE;
            }

            @Override
            public void onThrowable(Throwable t) {                
                t.printStackTrace();
            }
            
            
            @Override
            public Response onCompleted(Response rspns) throws Exception {
                log.addLine("Download completed.");  
                log.addLine("Processing " + fileName + "...");
                
                try {
                    PluginUncompressor pu = new PluginUncompressor(fileBytes, fileName, repoName, version);
                    if(pu.uncompress()){
                        log.addLine("Uncompressed succesfully.");
                    }
                    
                } catch(IOException e){
                    log.addLine("Error: There was a problem uncompressing the file.");
                    return rspns;
                }
               
                log.addLine("Completed.");
                backBtn.setText("Back");
                
                return rspns;
            }
        };
        
        
        Future<Response> f = asyncHttpClient.prepareGet(downloadUrl).setFollowRedirect(true).execute(handler);
        

    }

    
    public void installPlugin(String name, String shortName, String description, String homepage, String repoName, String repoUser){
        
        
        JDialog d = new JDialog();        
        d.setModal(true);
        d.setSize(500, 500);
        d.setTitle("Install " + name);
        d.setLocationRelativeTo(null);

        
        JPanel p = new JPanel();
        
        
        JPanel top = new JPanel();
        top.setBorder(new EmptyBorder(10, 10, 10, 10));
        top.setLayout(new BorderLayout());
        Title pluginTitle = new Title(name);
        
        top.add(pluginTitle, BorderLayout.NORTH);

        JLabel status = new JLabel("Getting versions...");
        top.add(status, BorderLayout.SOUTH);
        
        TupleList versions = new TupleList();
        JScrollPane scroll = new JScrollPane(versions);

        
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        
        String releasesUrl = "https://api.github.com/repos/"+repoUser+"/"+repoName+"/releases";
                
        asyncHttpClient.prepareGet(releasesUrl).execute(new AsyncCompletionHandler<Response>(){
            @Override
            public Response onCompleted(Response r) throws Exception{
                String json = r.getResponseBody();

                ArrayList<HashMap<String, Object>> map = Utils.parseArrayJson(json);
                
               
                if(map.size() == 0){
                    status.setText("This plugin doesn't have releases available for download.");

                } else {
                    
                    for(int i=0; i<map.size(); i++){
                        String tagName = (String)map.get(i).get("tag_name");
                        
                        HashMap<String, Object> assets = ((ArrayList<HashMap<String, Object>>)map.get(i).get("assets")).get(0);

                        
                        JButton installBtn = new JButton("Install");
                        String downloadUrl = (String)assets.get("browser_download_url");
                        int size = (int)assets.get("size");
                        
                       
                        installBtn.addActionListener(new ActionListener(){
                            @Override
                            public void actionPerformed(ActionEvent e) {                                
                                
                                setDownloadPanel(repoName, tagName, versions, downloadUrl, size, scroll);
                            }
                        });
                        
                        versions.addTuple(tagName, installBtn);
                    }
                }

                status.setText("There are "+map.size()+" available versions.");
                return r;
            }
            @Override
            public void onThrowable(Throwable t){
                JOptionPane.showMessageDialog(null, "Plugin information couldn't be retrieved.", "Error", JOptionPane.ERROR_MESSAGE);
                d.dispose();
                status.setText("<html><span style='color: red;'>There was an error.</span></html>");
            }
        });
        
        p.setLayout(new BorderLayout());
        p.add(top, BorderLayout.NORTH);
        
        
        p.add(scroll, BorderLayout.CENTER);
        
        
        d.add(p);
        d.setVisible(true);
        
    }
    
    
    private void updateContainer(String title, Component content, JPanel container){
        
        JPanel contentPanel = null;
        
        if(!(content instanceof JPanel)){
            contentPanel = new JPanel();
            contentPanel.add(content);
        } else {
            contentPanel = (JPanel)content;
        }
        
        JPanel scrollable = new JPanel();
        
        scrollable.setLayout(new BorderLayout());
        scrollable.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));        
        scrollable.add(getTitleWithMargin(title), BorderLayout.NORTH);
        scrollable.add(contentPanel);              

        container.removeAll();
        container.add(new JScrollPane(scrollable));
        
        container.revalidate();
        container.repaint();

    }
    
    
    private synchronized void showTagSearchResults(String json){        
        
        ArrayList<HashMap<String, Object>> tags = Utils.parseArrayJson(json);
         
        if(tags.size() == 0){
            cleanTagResults();
            return;
        }
        
        JPanel tagsPanel = split.getLeft();       
        
        JPanel tagContainer = new JPanel();


        for(int i=0; i<tags.size(); i++){
            String tag = (String)tags.get(i).get("short_name");          
            
            JButton tagBtn = new JButton("#"+tag);
            
            tagBtn.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    searchByTag(tag);
                }
            });
            
            tagContainer.add(tagBtn, Component.CENTER_ALIGNMENT);
        }
        
        updateContainer("Tags", tagContainer, tagsPanel);
        
    }
    

    
    private TupleList createPluginTupleList(){
        TupleList tuples = new TupleList();
        
        RemotePluginInstaller self = this;

        for(int i=0; i<pluginsResult.size(); i++){
            
            String name = "";
            String description = "";
            String homepage = "";
            String shortName = "";
            String repoUser = "";
            String repoName = "";
            
            if(pluginsResult.get(i).get("name") != null) name = (String)pluginsResult.get(i).get("name");
            if(pluginsResult.get(i).get("description") != null) description = (String)pluginsResult.get(i).get("description");
            if(pluginsResult.get(i).get("home_page") != null) homepage = (String)pluginsResult.get(i).get("home_page");
            if(pluginsResult.get(i).get("short_name") != null) shortName = (String)pluginsResult.get(i).get("short_name");
            if(pluginsResult.get(i).get("repo_user") != null) repoUser = (String)pluginsResult.get(i).get("repo_user");
            if(pluginsResult.get(i).get("repo_name") != null) repoName = (String)pluginsResult.get(i).get("repo_name");
            
            JButton seeBtn = new JButton("See");
            
            tuples.addTuple(name, seeBtn);
            
            Plugin plugin = new Plugin(name, description, homepage, shortName, repoUser, repoName);

            seeBtn.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println(plugin);
                    split.setRight(new RemotePluginInfo(plugin, self));
                }
            });
        }
        return tuples;
    }
    
    private void loadMorePlugins(String tagName, int page){
        System.out.println("Cargando mas plugins, pagina: " + page);
        
        String q = searchInput.getText().trim();
        
        String pluginUrl;
        
        if(tagName != null)
            pluginUrl = Utils.cleanServerUrl(currentServer) + "/tags/"+tagName+"/plugins?page=" + page;
        else
            pluginUrl = Utils.cleanServerUrl(currentServer) + "/plugins?q=" + q + "&page=" + page;
        
        
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();        
        
                
        Future<Response> fPlugins = asyncHttpClient.prepareGet(pluginUrl).execute(new AsyncCompletionHandler<Response>(){                
        @Override
        public Response onCompleted(Response r) throws Exception{       
       
            String json = r.getResponseBody();   
            
            ArrayList<HashMap<String, Object>> result = Utils.parseArrayJson(json);
            
            pluginsResult.addAll(result);
            
            if(result.size() == SEARCH_LIMIT){
                renderPluginResults(tagName, page, true);
            } else {
                renderPluginResults(tagName, page, false);
            }          
            
            return r;
        }
        @Override
        public void onThrowable(Throwable t){ 
            JOptionPane.showMessageDialog(null, "There was a connection error. Did you choose a working server?", "Error", JOptionPane.ERROR_MESSAGE);
        }                
        });
        
    }
    
    private void renderPluginResults(String tagName, int page, boolean hasMore){
        JPanel pluginsPanel = split.getCenter();
        
        String title = tagName == null? "Plugins search results" : "Plugins #"+tagName;
        
        if(pluginsResult.size() == 0){
            cleanPluginResults();
            return;
        }
        
        TupleList tuples = createPluginTupleList();
        
        if(hasMore){
            JButton loadMoreBtn = new JButton("Load more");
            loadMoreBtn.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadMoreBtn.setEnabled(false);
                    loadMorePlugins(tagName, page+1);
                }
            });

            tuples.addTuple("", loadMoreBtn);
        }
        
        JPanel p = new JPanel();
        p.add(tuples);
        
        updateContainer(title, p, pluginsPanel);
    }
    
    
    
    private synchronized void showPluginSearchResults(String json, String tagName){        
        pluginsResult.clear();
        pluginsResult = Utils.parseArrayJson(json);
        renderPluginResults(tagName, 1, pluginsResult.size() == SEARCH_LIMIT);
    }
    
    private void searchDirectLink(String url){
        
        
        System.out.println("LINK directo: " + url);
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        
        RemotePluginInstaller self = this;
        
        asyncHttpClient.prepareGet(url.trim()).execute(new AsyncCompletionHandler<Response>(){
            @Override
            public Response onCompleted(Response r) throws Exception{
                String json = r.getResponseBody();
                HashMap<String, Object> plugin = Utils.parseJson(json);
                
                String name = "";
                String description = "";
                String homepage = "";
                String shortName = "";
                String repoUser = "";
                String repoName = "";
                
                if(!plugin.containsKey("name")){
                    throw new Exception();
                }

                if(plugin.get("name") != null) name = (String)plugin.get("name");
                if(plugin.get("description") != null) description = (String)plugin.get("description");
                if(plugin.get("home_page") != null) homepage = (String)plugin.get("home_page");
                if(plugin.get("short_name") != null) shortName = (String)plugin.get("short_name");
                if(plugin.get("repo_user") != null) repoUser = (String)plugin.get("repo_user");
                if(plugin.get("repo_name") != null) repoName = (String)plugin.get("repo_name");

                Plugin plugin2 = new Plugin(name, description, homepage, shortName, repoUser, repoName);
                
                split.setRight(new RemotePluginInfo(plugin2, self));
                
                System.out.println(json);
                
                showPluginSearchResults(json, null);
                tagsSpinner.completeStep();
                pluginsSpinner.completeStep();
                return r;
            }
            @Override
            public void onThrowable(Throwable t){
                tagsSpinner.completeStep();
                pluginsSpinner.completeStep();
                JOptionPane.showMessageDialog(null, "URL doesn't belong to a plugin.", "Error", JOptionPane.ERROR_MESSAGE);
            }                
        });
    }
    
    
    private void querySearch(){
        
        String q = searchInput.getText().trim();
        
        if(q.length() == 0){
            cleanResults();
            tagsSpinner.completeStep();
            pluginsSpinner.completeStep();
            return;
        }
        
        if(q.toLowerCase().startsWith("http://") || q.toLowerCase().startsWith("https://")){
            cleanResults();
            searchDirectLink(q);
            return;
        }
        
        System.out.println("Searching: " + q);
        
        String tagUrl = Utils.cleanServerUrl(currentServer) + "/tags?q=" + q;
        String pluginUrl = Utils.cleanServerUrl(currentServer) + "/plugins?q=" + q;
        
        
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        
        
                
        Future<Response> fTags = asyncHttpClient.prepareGet(tagUrl).execute(new AsyncCompletionHandler<Response>(){                
            @Override
            public Response onCompleted(Response r) throws Exception{                        
                String json = r.getResponseBody();                
                showTagSearchResults(json);                
                tagsSpinner.completeStep();                
                return r;
            }
            @Override
            public void onThrowable(Throwable t){ 
                tagsSpinner.completeStep();
                JOptionPane.showMessageDialog(null, "There was a connection error. Did you choose a working server?", "Error", JOptionPane.ERROR_MESSAGE);
            }                
        });
        
        Future<Response> fPlugins = asyncHttpClient.prepareGet(pluginUrl).execute(new AsyncCompletionHandler<Response>(){                
        @Override
        public Response onCompleted(Response r) throws Exception{                        
            String json = r.getResponseBody();
            showPluginSearchResults(json, null);            
            pluginsSpinner.completeStep();
            return r;
        }
        @Override
        public void onThrowable(Throwable t){ 
            pluginsSpinner.completeStep();
            JOptionPane.showMessageDialog(null, "There was a connection error. Did you choose a working server?", "Error", JOptionPane.ERROR_MESSAGE);
        }                
        });
        
    }
    
    private void saveServerPreference(String url){        
        File prefFile = new File(MultimodalObserver.APP_PREFERENCES_FILE);
        prefs.setServer(url);
        PreferencesManager.save(prefs, prefFile);
    }
    
    private void checkServer(boolean popup){
        String url = serverInput.getText().trim();   

        if(!Utils.validateHTTP_URI(url)){
            JOptionPane.showMessageDialog(null, "Invalid URL.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        serverCheckButton.setText("Checking...");
        serverCheckButton.setEnabled(false);
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();

        Future<Response> f = asyncHttpClient.prepareGet(url).execute(new AsyncCompletionHandler<Response>(){                
            @Override
            public Response onCompleted(Response r) throws Exception{                        
                String json = r.getResponseBody();
                Map<String, Object> map = new HashMap<String, Object>();
                ObjectMapper mapper = new ObjectMapper();
                map = mapper.readValue(json, new TypeReference<Map<String, String>>(){});
                if(map.containsKey("mo_plugin_repository")){                            
                    serverCheckButton.setText(CHECK_SERVER_BUTTON_LABEL);
                    serverCheckButton.setEnabled(true);
                    currentServer = url;
                    setServerNotice();
                    saveServerPreference(url.trim());
                    
                    serverInput.setEnabled(false);
                    changeServer.setVisible(true);
                    serverCheckButton.setVisible(false);
                    
                    if(popup)
                        JOptionPane.showMessageDialog(null, "Server contains a plugin repository", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    throw new Exception();
                }                        
                return r;
            }
            @Override
            public void onThrowable(Throwable t){                        
                serverCheckButton.setText(CHECK_SERVER_BUTTON_LABEL);
                serverCheckButton.setEnabled(true);
                
                serverInput.setEnabled(true);
                changeServer.setVisible(false);
                serverCheckButton.setVisible(true);
                
                if(popup)
                    JOptionPane.showMessageDialog(null, "A plugin repository couldn't be found.", "Error", JOptionPane.ERROR_MESSAGE);
                setServerNotice();
            }                
        });

    }
    
    
   
    
    public JPanel createTopPanel(){
        JPanel result = new JPanel();
        
        JButton searchButton = new JButton("Search");
        
        searchButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                split.setPluginSpinner(pluginsSpinner);
                split.setTagSpinner(tagsSpinner);
                querySearch();
            }        
        });
        
        searchInput.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e) {
                
                split.setPluginSpinner(pluginsSpinner);
                split.setTagSpinner(tagsSpinner);
                
                if(searchDelay != null && searchDelay.isAlive()){
                    searchDelay.interrupt();
                }
                
                searchDelay = new Thread(() -> {    
                      try{
                        Thread.sleep(DELAY_MILLISECONDS);
                        querySearch();
                    } catch(InterruptedException ex){
                        // The thread was stopped
                    }                    
                });
                
                searchDelay.start();
            }

            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}        
        
        });
        
        serverCheckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkServer(true);
            }
        });
        
        serverInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
              if (e.getKeyCode()==KeyEvent.VK_ENTER){
                 checkServer(true);
              }
            }
        });
        
        TupleList inputs = new TupleList();
        
        changeServer.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                serverInput.setEnabled(true);
                changeServer.setVisible(false);
                serverCheckButton.setVisible(true);
            }
        });
        
        searchInput.setPlaceholder("e.g. video, jnativehook-mouse, http://server.com/plugins/jnativehook-mouse");
        
        changeServer.setVisible(false);
        
        JPanel inputBtnServer = new JPanel();
        JPanel inputBtnSearch = new JPanel();
        
        JPanel serverNoticePanel = new JPanel();
        serverNoticePanel.add(serverNotice);        
        
        inputBtnServer.add(serverInput);
        inputBtnServer.add(serverCheckButton);
        inputBtnServer.add(changeServer);
        
        inputBtnSearch.add(searchInput);
        inputBtnSearch.add(searchButton);
        
        inputs.addTuple("Use server", inputBtnServer);
        inputs.addTuple("Server status", serverNoticePanel);
        inputs.addTuple("Search", inputBtnSearch);

        
        result.add(inputs);
        
        return result;
    }
    
    
    public Title getTitleWithMargin(String txt){
        Title title = new Title(txt);
        Border border = title.getBorder();
        Border margin = new EmptyBorder(0,0,10,0);
        title.setBorder(new CompoundBorder(border, margin));
       
        return title;
    }
    

    public void cleanTagResults(){
        JPanel tags = split.getLeft();        
        updateContainer("Tags", new JLabel("No results"), tags);        
    }
   
    public void cleanPluginResults(){
        JPanel plugins = split.getCenter();        
        updateContainer("Plugins", new JLabel("No results"), plugins);        
    }
    
    
    public void cleanResults(){   
        cleanTagResults();
        cleanPluginResults();
    }
    
 
    
    public RemotePluginInstaller(){

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                
        tagsSpinner = new Spinner(1);
        pluginsSpinner = new Spinner(1);
        
        JPanel top = createTopPanel();
        split = new SplitPaneTriple();
        
        top.setAlignmentX(this.CENTER_ALIGNMENT);
        split.setAlignmentX(this.CENTER_ALIGNMENT);
        
        this.add(top);
        this.add(split);
        
        
        // Show initial message (related to the server status)
        setServerNotice();
        
        cleanResults();    
        
        try{
                        
            String server = prefs.getServer();
            
            if(server != null && server.length() > 0){
                
                serverInput.setText(server);
                checkServer(false);
                        
            }            
            
        } catch(Exception e){
        }        
        
    }    
    
}
