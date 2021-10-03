package com.project.com.project.maven.eclipse;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class useNN {
	private static final Logger log = LoggerFactory.getLogger(useNN.class);
	public static void main(String[] args) throws Exception {
	int height = 51;
	int width = 51;
	int channels = 1;
	
	List<Integer> labelList = Arrays.asList(0,1,2);
	File file = new File("E:\\\\Universidad\\\\tfg\\\\TrainedModels\\\\trained_knee_mark_model.zip");
	MultiLayerNetwork model =ModelSerializer.restoreMultiLayerNetwork(file);
	
	File image = new File("E:\\Universidad\\tfg\\Imagenes\\testing\\1\\12690.jpg");
	NativeImageLoader loader = new NativeImageLoader(height,width,channels);
	INDArray indImage = loader.asMatrix(image);
	INDArray inout2 = Nd4j.create(new int[]{ 1, 3 });
	DataNormalization scaler = new ImagePreProcessingScaler(0,1);
	scaler.transform(indImage);
	INDArray output = model.output(indImage);
	log.info(output.toString());
	float[] prob = output.toFloatVector();
	int res = 0;
	float aux = 0;
	for(int i = 0;i<3;i++) {
		if (aux <= prob[i]) {
			res = i;
			aux = prob[i];
		}
	}
	log.info(Integer.toString(res)+ ","+ Float.toString(aux));
	}
}
