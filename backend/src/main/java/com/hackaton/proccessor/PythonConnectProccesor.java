package com.hackaton.proccessor;

import com.hackaton.Tools;
import com.hackaton.model.FromModel;
import com.hackaton.service.Status;
import com.hackaton.service.StatusFactory;
import com.hackaton.service.Statuses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@Service
public class PythonConnectProccesor {
    @Autowired
    Tools tools;
    @Autowired
    Status status;
    @Autowired
    StatusFactory statusFactory;
    private Integer currPhotoCount = 0;
    private String defPhoto = "photos/test0.jpg";
    private void runPythonScript(String path) throws IOException, InterruptedException {
        File f = new File(defPhoto);
        String pythonScript = String.format("ml/venv/Scripts./python ml/run.py samples_folder=\"%s\" template_path=\"%s\"", path, f.getAbsolutePath());

        Process p = Runtime.getRuntime().exec(pythonScript);
        p.waitFor();

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));
        stdError.lines().forEach(System.out::println);
        String answer = stdInput.readLine();

        FromModel ready = tools.getMapper().reader().readValue(answer.replace("\'","\""), FromModel.class);
        System.out.println(answer);
        statusFactory.refreshStatus(Statuses.READY, ready, statusFactory.getStatusInstance().getTaskEmotion());
        //status.setMessage(answer);
        //status.setStatus(Statuses.READY);
    }
      
     

    public void processClientData(Integer photoCount, String imageString) throws IOException, InterruptedException {
        File folder = new File("photos");
        if(!folder.exists()){
            folder.mkdir();
        }
        String forDecode = imageString.substring(23);
        File f = new File(String.format("photos/test%d.jpg",currPhotoCount));
        byte[] s = tools.getDecoder().decode(forDecode);
        InputStream is = new ByteArrayInputStream(s);
        BufferedImage newBi = ImageIO.read(is);
        ImageIO.write(newBi, "jpg", f);
        currPhotoCount++;
        status.setStatus(Statuses.WRITING);
        if(currPhotoCount.equals(photoCount)){
            runPythonScript(folder.getAbsolutePath());
        }
        if(currPhotoCount >= photoCount){
            currPhotoCount = 0;
        }
    }

    public void setDefPhoto(String defPhoto) throws IOException {
        File folder = new File("default");
        if(!folder.exists()){
            folder.mkdir();
        }
        File f = new File("default/defaultPhoto.jpg");
        byte[] s = tools.getDecoder().decode(defPhoto.substring(23));
        InputStream is = new ByteArrayInputStream(s);
        BufferedImage newBi = ImageIO.read(is);
        ImageIO.write(newBi, "jpg", f);
        this.defPhoto = f.getAbsolutePath();
    }
}
