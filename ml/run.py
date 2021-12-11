from recognition_model import Recognition_model1
from emotion_recognition.emotion_model import Emotion_model1
import hydra
from omegaconf import DictConfig, OmegaConf
import os
import json
import operator


@hydra.main(config_path="conf", config_name="config")
def app(cfg: DictConfig) -> None:
    dir_name = os.path.dirname(os.path.abspath(__file__))
    model = Recognition_model1(**cfg.params)
    emo_model = Emotion_model1(**cfg.emo_params)
    input_dir = dir_name + cfg.input_folder

    template_path = input_dir + cfg.template_path
    samples_path = input_dir + cfg.samples_folder
    output_path = dir_name + cfg.output_folder

    prediction = model.gen_simularity_by_template(template_path, samples_path)
    emo_pred = emo_model.predict_from_folder(samples_path)
    if (len(emo_pred) == 0):
        max_emo = 'neutral'
    else:
        max_emo = max(emo_pred.items(), key=operator.itemgetter(1))[0]
    prediction["emo"] = max_emo
    print(f"{prediction}__1")
    print(f"{prediction}__2")
    with open(output_path + '/result.json', 'w') as f:
        json.dump(prediction, f)


if __name__ == "__main__":
    app()
