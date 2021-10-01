package org.apache.beam.examples;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation.Required;

import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.MapElements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTrial {

	public interface JsonTrialOptions extends PipelineOptions {

	    String getInputFile();

	    void setInputFile(String value);
	    
	    @Required
	    String getOutput();

	    void setOutput(String value);
	  }

	  @SuppressWarnings("serial")
	static void runJsonTransform(JsonTrialOptions options) {
	    Pipeline p = Pipeline.create(options);
	    
	    final Set<String> vowels = new HashSet<String>(Arrays.asList("a","e","i","o","u"));

	    p.apply("ReadLines", TextIO.read().from(options.getInputFile()))
	       	.apply("Employee Id Combination", MapElements.via(new SimpleFunction<String, String>(){
	       		
	       		@Override
	       		public String apply(String input) {
	       			ObjectMapper jacksonObjMapper = new ObjectMapper();
	                try {
	                    JsonNode jsonNode = jacksonObjMapper.readTree(input);
	                    
	                    String name = jsonNode.get("fname").textValue();
	                    int age = jsonNode.get("age").intValue();
	                    return name.substring(0,3).toUpperCase() + age;	                    
	                   
	                } catch (JsonProcessingException e) {
	                    e.printStackTrace();
	                    return null;
	                }
	                catch (IOException e) {
	                    e.printStackTrace();
	                    return null;
	                }
	       		}                
	       	}))
	        .apply("WriteCounts", TextIO.write().to(options.getOutput()));

	    p.run().waitUntilFinish();
	  }

	  public static void main(String[] args) {
		  
		  Date start = new Date();
		  JsonTrialOptions options =
	        PipelineOptionsFactory.fromArgs(args).withValidation().as(JsonTrialOptions.class);
		  
		  runJsonTransform(options);
	    
		  Date end = new Date();
		  System.out.println((end.getTime() - start.getTime())/1000 + " --------*********total milliseconds");
	  }

}
