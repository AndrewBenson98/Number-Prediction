package assignment2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;

/**BONUS ***
 * Feedforward class that takes in an image and uses an artificial neural
 * network to determine what nubmer is on the image
 * 
 * @author Andrew Benson (500745614)
 * @version November 27, 2016
 */
public class bonus {

	/**
	 * Main method goes through all the images, calculates what the predicted
	 * number is, then gives the classification rate.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
	
		String imageFiles= "labels.txt";
		
		// Counts the number of correct
		int correct = 0;

		// Create scanner object to read in the names of the image files
		Scanner picFiles = new Scanner(new File(imageFiles));

		// Create two array lists, one that will hold the name of the image, and
		// another to hold the correct value on the image (the number on the
		// image)
		ArrayList<String> labels = new ArrayList<String>();
		ArrayList<Integer> labelNumber = new ArrayList<Integer>();

		// Load the array lists with image names and numbers
		while (picFiles.hasNextLine()) {
			labels.add(picFiles.next());
			labelNumber.add(picFiles.nextInt());
		}

		// For each image, calculate and get the prediction.
		for (int i = 0; i < labels.size(); i++) {
			int prediction = feedforward("numbers/" + labels.get(i));

			// Compare the prediction and the actual number. If they are the
			// same, then add one to the number of correct predictions
			if (prediction == labelNumber.get(i)) {
				correct++;
			}
		}

		// Print out the correct number of predictions and the classification
		// rate
		System.out.println("Correct: " + correct);
		double rate = (correct * 1.0) / labels.size();
		System.out.printf("Classification Rate: %.3f", rate);
		picFiles.close();
	}

	
	/**
	 * The is the method that calculates the prediction of the image give
	 * different neuron weights
	 * 
	 * @param imageName
	 *            the name of the .png image
	 * @return the predicted output
	 * @throws IOException
	 */
	public static int feedforward(String imageName) throws IOException {

		// Create a 2D array that holds the weights for each neuron in the
		// hidden and output layer
		double[][] hiddenWeights = new double[300][785];
		double[][] outputWeights = new double[10][301];

		// Create arrays to hold the output
		double[] hiddenOutput = new double[300];
		double[] outputOutput = new double[10];

		// Read the weight values into 2D arrays
		try {

			// Create two scanners to read in the neuron weights
			Scanner hiddenIn = new Scanner(new File("hidden-weights.txt"));
			Scanner outputIn = new Scanner(new File("output-weights.txt"));

			// Populate the hidden layer array with weight values
			for (int i = 0; i < hiddenWeights.length; i++) {
				for (int j = 0; j < hiddenWeights[0].length; j++) {
					hiddenWeights[i][j] = hiddenIn.nextDouble();
				}
			}
			// Populate the output layer array with weight values
			for (int i = 0; i < outputWeights.length; i++) {
				for (int j = 0; j < outputWeights[0].length; j++) {
					outputWeights[i][j] = outputIn.nextDouble();
				}
			}

			// Step 2-- Read in the image
			BufferedImage img = ImageIO.read(new File(imageName));
			// get pixel data
			double[] dummy = null;
			double[] pixels = img.getData().getPixels(0, 0, img.getWidth(), img.getHeight(), dummy);

			// Step 3 --rescale values to 0 and 1

			// For each pixel, if the pixel value is over 127.5, then change it
			// to a 1. Otherwise, make it a zero
			for (int i = 0; i < pixels.length; i++) {
				if (pixels[i] > 150) {
					pixels[i] = 1;
				} else {
					pixels[i] = 0;
				}
			}

			// Step 4 Calculate outputs for hidden layer
			for (int i = 0; i < hiddenWeights.length; i++) {
				hiddenOutput[i] = clacNeuronVal(hiddenWeights[i], pixels);
			}

			// Step 5 Calculate outputs based on the calculations from the
			// hidden layer
			for (int i = 0; i < outputOutput.length; i++) {
				outputOutput[i] = clacNeuronVal(outputWeights[i], hiddenOutput);
			}

			// Display all 10 outputs
			//Count will display numbers 0-9 on the left hand side
			//Uncomment this code to see the result for all numbers 1-9
			
//			int count = 0;
//			for (double e : outputOutput) {
//
//				System.out.printf(count + "-> %.4f \n", e);
//				count++;
//			}

			
			// Find the largest of the output values. This corresponds to the
			// correct number prediction
			double largest = outputOutput[0];
			int largestIndex = 0;
			for (int i = 1; i < outputOutput.length; i++) {
				if (outputOutput[i] > largest) {
					largest = outputOutput[i];
					largestIndex = i;

				}
			}
			
			//Print out the network prediction
			System.out.println("The network prediction is " + largestIndex + ".");
			hiddenIn.close();
			outputIn.close();
			
			//Return the prediction
			return largestIndex;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("File is not found");
		}
		return -1;
	}

	/**
	 * Calculates the the weighted sum of a neuron 
	 * @param weights the weights and bias
	 * @param pixelValues the the values of every pixel in an image
	 * @return the weighted sum
	 */
	public static double clacNeuronVal(double[] weights, double[] pixelValues) {
		//The weighted sum
		double value = 0;

		//For each weight, multiply it by its corresponding pixel and add it to value
		for (int i = 0; i < weights.length - 1; i++) {
			value += weights[i] * pixelValues[i];

		}
		//Add the bias value
		value +=weights[weights.length-1];
		
		// Apply sigmoid function
		return 1 / (1 + Math.exp(-value));

	}
}
