
package edu.uthscsa.ric.plugins.mangoplugin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.io.FilenameUtils;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

import edu.uthscsa.ric.mango.MangoContext;
import edu.uthscsa.ric.mango.MangoData;
import edu.uthscsa.ric.mango.MangoPlugin;
import edu.uthscsa.ric.mango.ViewerController;
import edu.uthscsa.ric.mango.viewerslice.VolumeManager;
import edu.uthscsa.ric.utilities.AppLogger;
import edu.uthscsa.ric.utilities.ConsoleLogger;
import edu.uthscsa.ric.volume.Coordinate;
import edu.uthscsa.ric.volume.ImageVolume;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

public class ExamplePlugin implements MangoPlugin {
	
	static ConsoleLogger Logger = AppLogger.getConsoleLogger();
	static String  filename = "saved";
	
		
	public static void setFilename(String filename) {
		ExamplePlugin.filename = filename;
	}
	
	
	// Obtiene posición del crosshair
	public void colocarCrossHair(double x, double y, double z,VolumeManager viewer) {
		viewer.setMenuOption("Main Crosshairs", true);
		Coordinate coord = new Coordinate( x, y, z);
		viewer.setCurrentPosition(coord);		
	}

	
	
	@Override
	public void doOperation(MangoContext mango, VolumeManager viewer) {
		
		//Obtener imagen
		ImageVolume iv = viewer.getBaseVolume();
		viewer.setMenuOption("Orientation", false);
		URI uri = iv.getReadableHeader().getImageFile();
		
		try {
			String filename = FilenameUtils.getBaseName(uri.toURL().getPath());	
			if ((filename.equalsIgnoreCase(ExamplePlugin.filename))) {
								
			}
			else {
				// Eliminar el crosshair
				viewer.setMenuOption("Main Crosshairs", false);
				BufferedImage bff = viewer.getMainImage();
				
				//Procesar la imagen
				ImagePlus image = new ImagePlus("",bff);
				ImageProcessor ip = image.getProcessor();
				
				ip.erode();
				ip.dilate();
				ip.findEdges();	
				image.setProcessor(ip);
				
				IJ.save(image,"E:\\Universidad\\saved.jpg");
				File input = new File("E:\\Universidad\\saved.jpg");
				File output = new File("E:\\Universidad\\saved.dcm");
				dcm dcm = new dcm();
				dcm.jpg2Dcm(input, output, Logger); //Convierte imágenes en Dicom
				
				
				//Creamos un nuevo volumen para mostrar la nueva imagen
				ImageVolume nIV = mango.makeNewVolume();
				URI ivURI;
				ivURI = nIV.readFiles(output.toURI());
				VolumeManager nVM = mango.makeNewVolumeManagerURL(ivURI.toURL().toString());
				nVM.setMenuOption("Orientation", false);
				nVM.setMenuOption("Main Crosshairs", true);
				
				//Carga del clasificador automático
				int height = 51;
				int width = 51;
				int channels = 1;
				
				List<Integer> labelList = Arrays.asList(0,1,2);
				File file = new File("E:\\\\Universidad\\\\tfg\\\\TrainedModels\\\\trained_knee_clasificator_model.zip");//Ubicaciónclasificador
				MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork(file);
				 
				 NativeImageLoader loader = new NativeImageLoader(height,width,channels);
				 INDArray indImage = loader.asMatrix(input);
				 DataNormalization scaler = new ImagePreProcessingScaler(0,1);
				 scaler.transform(indImage);
				 INDArray output1 = model.output(indImage); //clasifica la imagen
				 Logger.println(output1.toString()); //muestra resultados
			}

		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Deprecated
	@Override
	public void doOperation(MangoData data, ViewerController controller) {

	}



	@Override
	public String getMinimumVersionSupported() {
		return null;
	}



	@Override
	public String getPluginName() {
			return "Find edges of knee images";
	}



	@Override
	public URL getPluginURL() {
		return null;
	}



	@Override
	public String getVersion() {
		return null;
	}



	@Override
	public boolean hasNewerVersion() {
		return false;
	}
}
