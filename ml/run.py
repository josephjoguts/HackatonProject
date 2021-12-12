from recognition_model import Recognition_model1
from emotion_recognition.emotion_model import Emotion_model1
import hydra
from omegaconf import DictConfig
import os
import json
import operator
import glob
from PIL import Image
import time
@hydra.main(config_path="conf", config_name="config")
def app(cfg : DictConfig) -> None:
    dir_name = os.path.dirname(os.path.abspath(__file__))
    model = Recognition_model1(**cfg.params)
    emo_model = Emotion_model1(**cfg.emo_params)
    input_dir = dir_name + cfg.input_folder


    template_path = cfg.template_path
    samples_path = cfg.samples_folder
    output_path = dir_name + cfg.output_folder

    images = []
    start_time = time.time()
    template_im = Image.open(template_path)
    for i,img_path in enumerate(glob.glob(samples_path + "/*.jpg")):
        img = Image.open(img_path)
        images.append(img)
    period1 = time.time() - start_time
    prediction = model.gen_simularity_by_template(template_im, images)
    period2 = time.time() - period1
    emo_pred = emo_model.predict_from_folder(images)
    period3 = time.time() - period2
    if (len(emo_pred[0]) == 0):
        max_emo = 'neutral'
    else:
        max_emo = max(emo_pred[0].items(), key=operator.itemgetter(1))[0]
    if max_emo == "disguest" or max_emo =="fear":
        max_emo = 'neutral'
    prediction["emo"] = max_emo
    prediction["num_emo_faces"] = emo_pred[1]
    prediction["open_images_time"] = period1
    prediction["face_recognition_time"] = period2
    prediction["emotion_recognition_time"] = period3
    print(prediction)
    print(prediction)

if __name__ == "__main__": 
    app()