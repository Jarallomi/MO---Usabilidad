package mo.updating;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

/**
 * Clase publica que permite el manejo de los archivos que se procesan desde los arreglos obtenidos desde los registros txt que indican los archivos y su fecha de modificacion
 */
public class fileClass {
    private String name;
    private Date modificationDate;

    public fileClass(String newFileName, Date newFileModificationDate){
        this.name = newFileName;
        this.modificationDate = newFileModificationDate;
    }

    public String getName(){
        return name;
    }

    public Date getDate(){
        return modificationDate;
    }


    /**
     * Metodo de clase que permite comparar entre 2 objetos fileClass con fin de saber si son iguales en nombre y el 1ero es anterior en fecha de modificacion
     * @param file1 Instancia de clase propia fileClass
     * @param file2 Instancia de clase propia fileClass
     * @return true Si son iguales en nombre y si file1 es anterior a file2 en la fecha de modificacion
     */
    public static boolean isSameFileButBeforeDate(fileClass file1, fileClass file2){
        if(file1.getName().equals(file2.getName()) && file1.getDate().before(file2.getDate()) ){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Metodo interno de fileClass que permite saber si 2 instancias de Fileclass tienen el mismo nombre
     * @param file1 fileClass a comparar
     * @param file2 fileClass a comparar
     * @return La salida correpsonde a true si los nombre son iguales, caso contrario sera false
     */
    public static boolean hasSameName(fileClass file1, fileClass file2){
        if (file1.getName() == file2.getName()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Metodo fileClassque permite averiguar si un objeto fileClass se encuentra solo por NOMBRE en un arreglo de objetos fileClass
     * @param fileclass Objeto sencillo fileClass que se busca encontrar por nombre
     * @param fileClassArray Arreglo de objetos fileClass
     * @return true si el objeto fileclass est presente por su nombre, false en caso contrario
     */
    public static Boolean isInArrayByName(fileClass fileclass, ArrayList<fileClass> fileClassArray){
        Boolean response = false;
        for (fileClass fileClassIterator : fileClassArray) {
            if (fileclass.getName().equals(fileClassIterator.getName())) {
                response = true;
                break;
            }
        }    
        if(response == true){
            return true;
        } else {
            return false;
        }
    }

}
