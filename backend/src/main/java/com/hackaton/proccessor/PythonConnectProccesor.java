package com.hackaton.proccessor;

import com.hackaton.Tools;
import com.hackaton.model.ClientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;

@Service
public class PythonConnectProccesor {
    @Autowired
    Tools tools;
    private Integer currPhotoCount = 0;
    private String defPhoto = "photos/test0.png";

    private void runPythonScript(String path) throws IOException {
        File f = new File(defPhoto);
        var p = Runtime.getRuntime().exec(String.format("python ml/main.py %s %s", path, f.getAbsolutePath()));
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        System.out.println(stdInput.readLine());
    }

    public void processClientData(Integer photoCount, String imageString) throws IOException {
        File folder = new File("photos");
        if(!folder.exists()){
            folder.mkdir();
        }
        String forDecode = imageString.substring(23);
        File f = new File(String.format("photos/test%d.png",currPhotoCount));
        byte[] s = tools.getDecoder().decode(forDecode);
        InputStream is = new ByteArrayInputStream(s);
        BufferedImage newBi = ImageIO.read(is);
        ImageIO.write(newBi, "png", f);
        currPhotoCount++;
        if(currPhotoCount.equals(photoCount)){
            runPythonScript(folder.getAbsolutePath());
        }
        if(currPhotoCount >= photoCount){
            currPhotoCount = 0;
        }
    }

    public void setDefPhoto(String defPhoto) throws IOException {
        File folder = new File("photos");
        if(!folder.exists()){
            folder.mkdir();
        }
        File f = new File("photos/defaultPhoto.png");
        byte[] s = tools.getDecoder().decode(defPhoto);
        InputStream is = new ByteArrayInputStream(s);
        BufferedImage newBi = ImageIO.read(is);
        ImageIO.write(newBi, "png", f);
        this.defPhoto = f.getAbsolutePath();
    }
}
