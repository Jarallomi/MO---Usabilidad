package mo.updating;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

/**
 * Clase que permite la escritura del archivo .txt con las notas de version
 */
public class updaterVersionNotesRegister {
    
    /**Metodo que permite a traves de un string la creacion/sobreescritura de un nuevo archivo de notas de version
    *PAra ser usada con posterioridad
    *@param notes es el string escrito con las notas de la nueva version
    *@return void
    */
    public static void newNotesVersion(String notes){
        //Se considera la revision de la existencia de un nuevo archivo de notas de version
        Path file = Paths.get("./versionNotes.txt");
        try {
            // Crea el archivo si no existe
            if (!Files.exists(file)) {
                Files.createFile(file);
                System.out.println("(updaterVersionNotesRegister.java) - El registro de notas no existia, Se ha creado el archivo 'versionNotes.txt'\n");
            } else {
                System.out.println("El archivo 'versionNotes.txt' ya existe. Se borra para crear uno nuevo\n");
                Files.delete(file);
                Files.createFile(file);
                System.out.println("(updaterVersionNotesRegister.java) - Se ha creado otro archivo 'versionNotes.txt\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Se crea y escribe el nuevo archivo de notas
        try{
            BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            try {
                //Escritura
                writer.write(notes);     
                writer.close();    
                //DEBUG: borrado de archivo
                Files.deleteIfExists(file);          
                } catch (IOException e) {
                    e.printStackTrace();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo que permite obtener las notas de version desde el repositorio remoto. Llamado por primera vez en el controlador
     *  de la vista de confirmacion
     * @param aToken Uno de las tres partes del token usado para el acceso al repositorio
     * @param bToken Uno de las tres partes del token usado para el acceso al repositorio
     * @param cToken Uno de las tres partes del token usado para el acceso al repositorio
     * @param remoteNotesApiUrl String con la url para la obtencion del texto que contiene las notas de version remotas
     * @return un String con todo el texto de VersionNotes.txt del repositorio
     * @throws IOException
     */
    public static String getRemoteVersionNotes(String aToken, String bToken, String cToken, String remoteNotesApiUrl) throws IOException{
        
        String versionNotes;
        //Token
        String githubToken = aToken + bToken + cToken; 

        //Solicitud que se hace a la API de Github
        String apiUrl = String.format(remoteNotesApiUrl);
        
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //Se ejecuta la conexion con los permisos otorgados por Token
        connection.setRequestProperty("Authorization", "token " + githubToken);
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        //Si la solicitud tiene exito (codigo 200)
        if (responseCode == 200) {

            System.out.println("(updaterVersionNotesRegister.java) - Ingresamos sin problemas al Repositorio para buscar las notas de version \nLink: " + apiUrl + "\n");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            //JSON respuesta con contenidos de la solicitud
            //La respuesta debera tener el contenido raw del txt de notas de version del repositorio
            String jsonResponse = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            reader.close();

            System.out.println("(updaterVersionNotesRegister.java) - JSON DE SOLICITUD A TXT REMOTO versionNotes.txt\n----\n");
            System.out.println(jsonResponse);
            
            //Se obtiene la version remota a traves del txt que lo indica
            versionNotes = jsonResponse.toString();
            
            //System.out.println("DEL JSON SE OBTIENE: " + versionNotes + "\n");
            return versionNotes;
            
            
        } else {
            System.err.println("(updaterVersionNotesRegister.java) - Error en la solicitud de obtencion de notas de version remotas. Codigo: " + responseCode);
            versionNotes = "NULL";
            return versionNotes;
        }
    }
    
}
