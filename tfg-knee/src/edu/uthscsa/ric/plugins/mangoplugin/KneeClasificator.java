package com.project.com.project.maven.eclipse;

import java.io.File;
import java.util.Random;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.listener.impl.LogRecordListener;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KneeClasificator {
	private static final Logger log = LoggerFactory.getLogger(KneeClasificator.class);
	
	
	public static void main(String[] args) throws Exception {
        int height = 51;
        int width = 51;
        int nChannels = 1; // Number of input channels
        int outputNum = 3; // The number of possible outcomes
        int batchSize = 600; // Test batch size
        int nEpochs = 32; // Number of training epochs
        int seed = 123;
        Random rngseed = new Random(123);
        String knee_dataset = "E:\\Universidad\\tfg\\DataSet\\";
        
        File trainData = new File(knee_dataset + "training");
        File testData = new File(knee_dataset + "testing");
        
        FileSplit train = new FileSplit(trainData,NativeImageLoader.ALLOWED_FORMATS,rngseed);
        FileSplit test = new FileSplit(testData,NativeImageLoader.ALLOWED_FORMATS,rngseed);
         
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        
        ImageRecordReader recordReader = new ImageRecordReader(height,width,nChannels,labelMaker);
        
        recordReader.initialize(train);
        
        DataSetIterator dsi = new RecordReaderDataSetIterator(recordReader,batchSize,1,outputNum);
        
        DataNormalization scaler = new ImagePreProcessingScaler(0,1);
        scaler.fit(dsi);
        dsi.setPreProcessor(scaler);
        
                        
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .seed(seed)
            .l2(0.0005)
            .weightInit(WeightInit.XAVIER)
            .updater(Updater.ADAM)
            .list()
            .layer(0,new ConvolutionLayer.Builder(5, 5)
                    //nIn and nOut specify depth. nIn here is the nChannels and nOut is the number of filters to be applied
                    .nIn(nChannels)
                    .stride(1,1)
                    .nOut(20)
                    .activation(Activation.IDENTITY)
                    .build())
            .layer(1,new SubsamplingLayer.Builder(PoolingType.MAX)
                    .kernelSize(2,2)
                    .stride(2,2)
                    .build())
            .layer(2,new ConvolutionLayer.Builder(5, 5)
                    //Note that nIn need not be specified in later layers
                    .stride(1,1)
                    .nOut(50)
                    .activation(Activation.IDENTITY)
                    .build())
            .layer(3,new SubsamplingLayer.Builder(PoolingType.MAX)
                    .kernelSize(2,2)
                    .stride(2,2)
                    .build())
            .layer(4,new DenseLayer.Builder().activation(Activation.RELU)
                    .nOut(500).build())
            .layer(5,new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .nOut(outputNum)
                    .activation(Activation.SOFTMAX)
                    .build())
            .setInputType(InputType.convolutionalFlat(51,51,1))
            .build();
            
                
    	MultiLayerNetwork model = new MultiLayerNetwork(conf);
    	model.init();
    	model.setListeners(new ScoreIterationListener(10));

    	for (int i = 0; i<nEpochs;i++) {
    		model.fit(dsi);
    	}

    	recordReader.reset();
    	recordReader.initialize(test);
    	DataSetIterator dsiTest = new RecordReaderDataSetIterator(recordReader,100,1,outputNum);
    	scaler.fit(dsiTest);
        dsi.setPreProcessor(scaler);
    	Evaluation eval = new Evaluation(outputNum);
    	
    	while(dsiTest.hasNext()) {
    		DataSet next = dsiTest.next();
    		INDArray output = model.output(next.getFeatureMatrix());
    		eval.eval(next.getLabels(),output);
    	}
    	log.info(eval.stats());
    	
    	File trainedModel = new File("E:\\Universidad\\tfg\\TrainedModels\\trained_knee_clasificator_model.zip");
    	 
    	 boolean saveUpdater = false;
    	 
    	 ModelSerializer.writeModel(model, trainedModel, saveUpdater);
	}
}
