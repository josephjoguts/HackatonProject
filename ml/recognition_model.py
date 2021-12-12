from facenet_pytorch import MTCNN, InceptionResnetV1
from PIL import Image
from torch.nn import CosineSimilarity
import glob
import numpy as np
from distance_metrics.distance_metric import count_perseptual_dist
import torch


class Recognition_model1:
    def __init__(self,
                 image_size: int = 256,
                 return_all: bool = False,
                 threshold=0.7,
                 offset=1,
                 distance_metric=CosineSimilarity(),
                 simularity_strategy="pixel_mse",  # есть ещё perseptual_loss
                 simularity_threshold=0.04):  # если perseptual_loss лучше юзать 1e-07
        self.image_size = image_size
        self.keep_all = return_all
        self.mtcnn = MTCNN(image_size=self.image_size)
        self.resnet = InceptionResnetV1(pretrained='vggface2').eval()
        self.distance_metric = distance_metric
        self.threshold = threshold
        self.offset = offset
        self.simularity_strategy = simularity_strategy
        self.simularity_threshold = simularity_threshold

    def crop_img(self, image, save_path=None):
        img_cropped = self.mtcnn(image, save_path=save_path)
        return img_cropped

    def gen_emb(self, img_cropped):
        if img_cropped == None:
            return None
        return self.resnet(img_cropped.unsqueeze(0))

    def gen_simularity_by_template(self,
                                   template,
                                   samples):
        loss = torch.nn.MSELoss()
        tamplate_crop = self.crop_img(template)
        template_emb = self.gen_emb(tamplate_crop)
        sampels_emb = []
        samples_crop = []
        crop_dists = []
        dist = []
        for i, img in enumerate(samples):
            if (i % self.offset == 0):
                sample_crop = self.crop_img(img)
                sampel_emb = self.gen_emb(sample_crop)
                if sampel_emb != None:
                    samples_crop.append(sample_crop)
                    sampels_emb.append(sampel_emb)
                    if (i != 0):
                        if self.simularity_strategy == "pixel_mse":
                            crop_dists.append(loss(sample_crop, samples_crop[-2]))
                        else:
                            crop_dists.append(count_perseptual_dist(sample_crop, samples_crop[-2]))
                        # print("prseptual dist: {}".format(count_perseptual_dist(sample_crop, samples_crop[-2])))
        for se in sampels_emb:
            dist.append(self.distance_metric(se, template_emb).detach().numpy())
        if len(dist) == 0:
            mean_dist = 0
        else:
            if len(crop_dists) > 0:
                mean_sim_dist = np.array(crop_dists).mean()
            else:
                mean_sim_dist = 0
            mean_dist = np.array(dist).squeeze(1).mean()
        data = {"verified": "Yes" if mean_dist > self.threshold and mean_sim_dist > self.simularity_threshold else "No",
                "mean_dist": round(float(mean_dist), 3)}
        return data
