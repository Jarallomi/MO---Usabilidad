/*
 * Datos.java
 */
package audiovisualizationplugin;

import java.util.ArrayList;
import java.util.List;

public class Datos {

    byte[] bits ;
    boolean formato;  // para el formato bigEndian y littleEndian
    double mayor, menor;

    public  Datos(int tamano, boolean formato) {
	bits = new byte[tamano];
	this.formato = formato;
	mayor = 0;
	menor = 0;
    }

    public void llenarByte(byte[] bits) {
        this.bits = bits;
    }

    /* ejemplo bits[2]=2 (00000010) bits[3]=3 (00000011)
     se aplica bits[2]<<8 o sea 10 00000000 , luego 11111111(0x000000FF) & bits[3]|bits[2]
     en total da 10 00000011  que es el numero 515 , este es un short de 16 bits , han entrado
     dos bytes en uno (short[i]=contacenar byte[i]+byte[i+1])
     los valores negativos estan en complemento a 2
     */

    public List<Double> convertirByteADouble(int size) {
        List<Double> arrayDouble = new ArrayList<Double>();
        //double[] arrayDouble = new double[bits.length/2];
        if (formato==true) {
            int temp = 0x00000000;
            for (int i = 0, j = 0; j < size ; j++, temp = 0x00000000) {
                temp=(int)bits[i++]<<8;//System.out.println("temp = "+ temp);
                temp |= (int) (0x000000FF & bits[i++]);
                //arrayDouble[j]=(double)temp;
                arrayDouble.add(j, (double)temp);
            }
            return arrayDouble;
        }
        if(formato==false) {  // si el formato es littleEndian
            int temp = 0x00000000;
            for (int i = 0, j = 0; j < size ; j++, temp = 0x00000000) {
                temp=(int)bits[i+1]<<8;//System.out.println("temp = "+ temp);
                temp |= (int) (0x000000FF & bits[(i)]);
                i=i+2;
                arrayDouble.add(j, (double)temp);
		//calcular mayor y menor esto me servira para establecer
		//los parametros en el eje y para la grafica
                if(mayor<arrayDouble.get(j)) {
                    mayor=arrayDouble.get(j);
                }
                if(menor>arrayDouble.get(j)) {
                    menor=arrayDouble.get(j);
                }
            }
            return arrayDouble;
        } else {
            System.out.println("Orden de Bytes desconocido o no soportado");
        }
	return arrayDouble;
    }
}
		
	
	
	
