package controllers;

import play.*;
import play.mvc.*;
import utils.VideoFormats;
import views.html.*;
import models.S3File;
import play.db.ebean.Model;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http;
import views.html.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.amazonaws.services.s3.model.AccessControlList;
import com.brightcove.zencoder.client.ZencoderClient;
import com.brightcove.zencoder.client.ZencoderClientException;
import com.brightcove.zencoder.client.model.*;
import com.brightcove.zencoder.client.request.*;
import com.brightcove.zencoder.client.response.*;

import utils.VideoFormats;

public class Application extends Controller {
	
	//Fields
	private static String extension;

    public static Result index() {
        List<S3File> uploads = new Model.Finder(UUID.class, S3File.class).all();     
        return ok(index.render(uploads));
    }

    public static Result upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart uploadFilePart = body.getFile("upload");
        if (uploadFilePart != null) {
            S3File s3File = new S3File();
            s3File.name = uploadFilePart.getFilename();
            s3File.file = uploadFilePart.getFile();
            s3File.rawname = s3File.name.split("\\.(?=[^\\.]+$)")[0];
            extension = s3File.name.split("\\.(?=[^\\.]+$)")[1];
            if(IsVideoFormat(extension)){
            	s3File.save(); //Save file in DataBase and Upload to Amazon S3
            	ConvertUsingZencoder(s3File);
            	return redirect(routes.Application.index());
            }else{
                return badRequest("Formato incorreto. Somente arquivos de vídeo serão aceitos.");
            }
        }
        else {
            return badRequest("Erro no envio do arquivo.");
        }
    }
    
    private static void ConvertUsingZencoder(S3File s3File){
    	try{
    	
    	ZencoderClient client = new ZencoderClient("67638c1e3e00efc24c2249cfb9b36bd1");
    	
    	
    	ZencoderCreateJobRequest job = new ZencoderCreateJobRequest();
    	job.setInput(s3File.getFileUrl());
    	ArrayList<ZencoderOutput> outputs = new ArrayList<ZencoderOutput>();

    	ZencoderOutput output1 = new ZencoderOutput();
    	output1.setFormat(ContainerFormat.FLV);
    	output1.setPublic(true);
    	output1.setCredentials("s3videoserver");
    	output1.setUrl(s3File.getOutputUrl());
    	outputs.add(output1);

    	job.setOutputs(outputs);
    	ZencoderCreateJobResponse response = client.createZencoderJob(job);
    	
    	} catch (Exception e){
    		
    		System.out.println(e.toString());
    	}
    	
    }
    
    private static boolean IsVideoFormat(String fileExtension){
    	
    	int formatsCount = VideoFormats.Instance().videoformats.length;
    	
    	if (!fileExtension.equals("") && fileExtension!=null){
    		for (int i=0; i<formatsCount; i++){
    			
    			if (VideoFormats.Instance().videoformats[i].equals(fileExtension)){
    				return true;
    			}  			
    		}
    		return false;
    	}
    	return false;
    }

}
