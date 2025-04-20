/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package commoninputsclustering;

import com.martiansoftware.jsap.JSAPException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author brodo
 */
public class CommonInputsClustering {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            ClusteringManagerWG.fromTxList2ClusteringMap(args[0],args[1]);
        } catch (InterruptedException ex) {
            Logger.getLogger(CommonInputsClustering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CommonInputsClustering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InconsistencyException ex) {
            Logger.getLogger(CommonInputsClustering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(CommonInputsClustering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CommonInputsClustering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(CommonInputsClustering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(CommonInputsClustering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CommonInputsClustering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(CommonInputsClustering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSAPException ex) {
            Logger.getLogger(CommonInputsClustering.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
