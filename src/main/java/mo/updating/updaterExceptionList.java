package mo.updating;

/**
 * Clase que sirve para contener las palabras que los registros de archivos deben evitar obtener
 */
public class updaterExceptionList {
    
    //Palabras que el creador de registro debe evitar considerar
    public static final String[] fileNamesToAvoid = {"java\\mo\\updating",
    "Register.txt",
    "RemoteRegister.txt",
    "FileRegister.txt",
    "Repo.zip",
    ".gradle",
    ".git",
    "\\bin",
    "\\build",
    ".vscode",
    "preferences.xml",
    "LaunchMO.jar",
    ".classpath",
    "CreateRegister.jar",
    "args.up",
    "\\ups\\"
    };

    //Palabras que el extractor de .zip debe evitar al descomprimir 
    public static final String[] filesToNotExtract = {"/ups/", "/updating/", "LaunchMO"};
}
