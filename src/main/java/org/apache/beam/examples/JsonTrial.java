package org.apache.beam.examples;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.extensions.jackson.ParseJsons;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation.Required;

import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;

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

	    PCollection<String> lines= p.apply(TextIO.read().from(options.getInputFile()));
	    
	    PCollection<Employee> employees = lines.apply(ParseJsons.of(Employee.class)).setCoder(SerializableCoder.of(Employee.class));
	    
//	    PCollection<Employee> employeeNames = employees.apply(MapElements.into(TypeDescriptor.of(Employee.class)).via(e -> e))
//	    		.apply(Filter.by(e -> vowels.contains(e.getFname().substring(0, 1).toLowerCase()) && e.getAge()>39 && e.getGender().equalsIgnoreCase("Female")));
	    
	    PCollection<String> employeeNames1 = employees.apply(MapElements.into(TypeDescriptor.of(Employee.class)).via(e -> e))
	    		.apply(MapElements.into(TypeDescriptors.strings()).via(em -> em.getFname().substring(0, 3)+em.getAge()));
	    
	    employeeNames1.apply(MapElements.into(TypeDescriptors.strings()).via(Object::toString))
	    .apply(TextIO.write().to(options.getOutput()));	  

	    p.run().waitUntilFinish();
	  }

	  public static void main(String[] args) {
		  
		  Date start = new Date();
		  JsonTrialOptions options =
	      PipelineOptionsFactory.fromArgs(args).withValidation().as(JsonTrialOptions.class);
		  
		  runJsonTransform(options);
	    
		  Date end = new Date();
		  System.out.println((end.getTime() - start.getTime())/1000 + " --------*********total seconds");
	  }

}
