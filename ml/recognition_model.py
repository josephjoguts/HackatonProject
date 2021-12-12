from facenet_pytorch import MTCNN, InceptionResnetV1
from PIL import Image
from torch.nn import CosineSimilarity
import glob
import numpy as np

class Recognition_model1:
    def __init__(self,
        image_size: int = 256,
        return_all: bool = False,
        threshold = 0.7,
        offset = 1,
        distance_metric = CosineSimilarity()):
        self.image_size = image_size
        self.keep_all = return_all
        self.mtcnn =  MTCNN(image_size=self.image_size)
        self.resnet = InceptionResnetV1(pretrained='vggface2').eval()
        self.distance_metric = distance_metric
        self.threshold = threshold
        self.offset = offset

    def crop_img(self, image, save_path = None):
        img_cropped = self.mtcnn(image, save_path=save_path)
        return img_cropped
    
    def gen_emb(self, image):
        img_cropped = self.crop_img(image)
        if img_cropped == None:
            return None
        return self.resnet(img_cropped.unsqueeze(0))

    def gen_simularity_by_template(self,
     template_path,
     samples_folder_path):
        template_im = Image.open(template_path)
        template_emb = self.gen_emb(template_im)
        sampels_emb = []
        dist = []
        for i,img_path in enumerate(glob.glob(samples_folder_path + "/*.jpg")):
            if (i % self.offset == 0):
                sampel_im = Image.open(img_path)
                sampel_emb = self.gen_emb(sampel_im)
                if sampel_emb != None:
                    sampels_emb.append(sampel_emb)
        for se in sampels_emb:
            dist.append(self.distance_metric(se, template_emb).detach().numpy())
        if len(dist) == 0:
            mean_dist = 0
        else:
            mean_dist = np.array(dist).squeeze(1).mean()
        data = {"verified": "Yes" if mean_dist > self.threshold else "No",
        "mean_dist": round(float(mean_dist),3)}
        return data
