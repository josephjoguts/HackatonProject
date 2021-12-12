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
    private String defPhoto = "../photos/test0.png";
    //TODO путь к папке с картинками видео
    //TODO user default photo
    private void runPythonScript(String path) throws IOException {
        var p = Runtime.getRuntime().exec(String.format("python ../ml/run.py samples_folder=%s template_path=%s 2>log.txt", path, defPhoto));
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        System.out.println(stdInput.readLine());
    }

    public void processClientData(Integer photoCount, String imageString) throws IOException {
        File folder = new File("../photos");
        if(!folder.exists()){
            folder.mkdirs();
        }
        File f = new File(String.format("../photos/test%d.png",currPhotoCount));
        byte[] s = tools.getDecoder().decode(imageString);
        InputStream is = new ByteArrayInputStream(s);
        BufferedImage newBi = ImageIO.read(is);
        ImageIO.write(newBi, "png", f);
        currPhotoCount++;
        if(currPhotoCount == photoCount){
            runPythonScript(folder.getAbsolutePath());
        }
    }

    public void setDefPhoto(String defPhoto) throws IOException {
        File f = new File("../defaultPhoto.png");
        byte[] s = tools.getDecoder().decode(defPhoto);
        InputStream is = new ByteArrayInputStream(s);
        BufferedImage newBi = ImageIO.read(is);
        ImageIO.write(newBi, "png", f);
        this.defPhoto = f.getAbsolutePath();
    }
}
