from recognition_model import Recognition_model1
from emotion_recognition.emotion_model import Emotion_model1
from distance_metrics.distance_metric import count_mse_dist, count_perseptual_dist, get_hog_distance
import hydra
from omegaconf import DictConfig
import os
import json
import operator
import glob
from PIL import Image


@hydra.main(config_path="conf", config_name="config")
def app(cfg: DictConfig) -> None:
    dir_name = os.path.dirname(os.path.abspath(__file__))
    model = Recognition_model1(**cfg.params)
    emo_model = Emotion_model1(**cfg.emo_params)
    input_dir = dir_name + cfg.input_folder

    template_path = cfg.template_path
    samples_path = cfg.samples_folder
    output_path = dir_name + cfg.output_folder
    images = []
    template_im = Image.open(template_path)
    for i, img_path in enumerate(glob.glob(samples_path + "/*.jpg")):
        img = Image.open(img_path)
        images.append(img)
    prediction = model.gen_simularity_by_template(template_im, images)
    emo_pred = emo_model.predict_from_folder(images)
    if len(emo_pred) == 0:
        max_emo = 'neutral'
    else:
        max_emo = max(emo_pred.items(), key=operator.itemgetter(1))[0]
    prediction["emo"] = max_emo
    print(prediction)
    print(prediction)


if __name__ == "__main__":
    app()
