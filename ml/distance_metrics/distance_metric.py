import torch
import torchvision.transforms as transforms
import torch.nn as nn
import torchvision.models as models
import cv2
import numpy as np
from numpy import asarray

def count_mse_dist(img1, img2):
    val_transform = transforms.Compose([
        transforms.Resize((256, 256)),
        transforms.ToTensor()])

    ten1 = val_transform(img1).unsqueeze(0)
    ten2 = val_transform(img2).unsqueeze(0)
    with torch.no_grad():
        loss = torch.nn.MSELoss()
        output = loss(ten1, ten2)
    return output

def gram_matrix(tensor):  
  _,d,h,w=tensor.size()  
  tensor=tensor.view(d,h*w)  
  gram=torch.mm(tensor,tensor.t()) / (d*h*w)  
  return gram

def count_perseptual_dist(img1, img2):
    loss = nn.MSELoss()
    vgg16 = models.vgg16(pretrained=True)
    blocks = []
    blocks.append(vgg16.features[:4].eval())
    for bl in blocks:
        for p in bl.parameters():
            p.requires_grad = False
    blocks = torch.nn.ModuleList(blocks)

    img1= img1.reshape(1,3, 256,256)
    img2 = img2.reshape(1,3, 256,256)
    pred1 = blocks[0](img1)
    pred2 = blocks[0](img2)

    with torch.no_grad():
        output = loss(gram_matrix(pred1), gram_matrix(pred2))
    return output

def get_hog_distance(img1, img2):
    loss = nn.MSELoss()
    img1 = asarray(img1.permute(1,2,0))
    img2 = asarray(img2.permute(1,2,0))
    hog = cv2.HOGDescriptor()
    gray1 = cv2.cvtColor(img1, cv2.COLOR_BGR2GRAY)
    gray2 = cv2.cvtColor(img2, cv2.COLOR_BGR2GRAY)
    fv1 = hog.compute(gray1, winStride=(8, 8), padding=(0, 0))
    fv2 = hog.compute(gray2, winStride=(8, 8), padding=(0, 0))

    with torch.no_grad():
        output = loss(torch.tensor(fv1), torch.tensor(fv2))
    return output



"""    val_transform = transforms.Compose([
        transforms.Resize((256, 256)),
        transforms.ToTensor()])
    ten1 = val_transform(img1).unsqueeze(0)
    ten2 = val_transform(img2).unsqueeze(0)"""