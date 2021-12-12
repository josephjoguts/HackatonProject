import cv2
import torch
import torchvision.transforms as transforms
from PIL import Image
from .model import *
from facenet_pytorch import MTCNN
from random import choice
import glob
import operator
import numpy as np
from collections import defaultdict
import os

def load_trained_model(model_path):
    model = Face_Emotion_CNN()
    model.load_state_dict(torch.load(model_path, map_location=lambda storage, loc: storage), strict=False)
    return model

class Emotion_model1:
    def __init__(self,image_size: int = 48,
        crop_strategy = "MTCNN",):
        dir_name = os.path.dirname(os.path.abspath(__file__))
        self.crop_strategy = crop_strategy
        if (crop_strategy == "MTCNN"):
            self.cropper =  MTCNN(image_size=image_size)
        else:
            self.cropper = cv2.CascadeClassifier(dir_name + '/models/haarcascade_frontalface_default.xml')
        self.model = load_trained_model(dir_name + '/models/FER_trained_model.pt')
        self.emotion_dict = {0: 'neutral', 1: 'happiness', 2: 'surprise', 3: 'sadness',
                    4: 'anger', 5: 'disguest', 6: 'fear'}
    def gen_emotion(self):
        return choice(self.emotion_dict)

    def predict_from_folder(self, samples):
        val_transform = transforms.Compose([
        transforms.ToTensor()])
        emo_score = defaultdict(list)
        num_emo_faces = 0 
        for img in samples:
            if self.crop_strategy == "MTCNN":
                crop = self.cropper(img)
                if crop != None:
                    num_emo_faces += 1
                    crop = crop.permute(1,2,0).detach().numpy()
                    gray_crop = cv2.cvtColor(crop, cv2.COLOR_BGR2GRAY)
                    X = gray_crop/256
                else:
                    continue
            else:
                gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
                faces = self.cropper.detectMultiScale(img)
                if len(faces) == 0:
                    continue
                resize_frames = {}
                for i,(x, y, w, h) in enumerate(faces):
                    resize_frames[w*h] = cv2.resize(gray[y:y + h, x:x + w], (48, 48))
                resize_frame = max(resize_frames.iteritems(), key=operator.itemgetter(1))[0]
                X = resize_frame/256
            X = Image.fromarray((gray_crop))
            X = val_transform(X).unsqueeze(0)
            with torch.no_grad():
                self.model.eval()
                log_ps = self.model.cpu()(X)
                ps = torch.exp(log_ps)
                for i,p in enumerate(ps.detach().numpy().squeeze(0)):
                    emo_name = self.emotion_dict[int(i)]
                    emo_score[emo_name].append(p)
        for k in emo_score.keys():
            emo_score[k] = np.mean(emo_score[k])
        return [dict(emo_score), num_emo_faces]
