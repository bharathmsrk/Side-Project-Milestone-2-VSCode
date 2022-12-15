import cv2
import os
import numpy as np
from datetime import datetime
import PIL
from Face_Recognition_script import img_to_encoding, who_is_it
from tensorflow.keras.models import model_from_json
import tensorflow as tf

json_file = open('src/main/face_recognition/keras-facenet-h5/model.json', 'r')
loaded_model_json = json_file.read()
json_file.close()
model = model_from_json(loaded_model_json)
model.load_weights('src/main/face_recognition/keras-facenet-h5/model.h5')
FRmodel = model

path = '/Users/bharathradhakrishnan/side-project-bharathmsrk/input/ImageData'
customer_Image_List = []
customer_Name_List = []
list = os.listdir(path)
for name in list:
    a = f'{path}/{name}'
    customer_Image_List.append(a)
    customer_Name_List.append(os.path.splitext(name)[0])

def who_is_it(encoding, database, model):
    min_dist = 100
    for (name, db_enc) in database.items():
        dist = tf.math.sqrt(tf.reduce_sum(tf.square(tf.subtract(encoding,db_enc))))
        if dist < min_dist:
            min_dist = dist
            identity = name
    return min_dist, identity

def img_path_to_encoding(image_path, model):
    print(image_path)
    img = tf.keras.preprocessing.image.load_img(image_path, target_size=(160, 160))
    img = np.around(np.array(img) / 255.0, decimals=12)
    x_train = np.expand_dims(img, axis=0)
    embedding = model.predict_on_batch(x_train)
    return embedding / np.linalg.norm(embedding, ord=2)

def img_to_encoding(img, model):
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    img = PIL.Image.fromarray(img)
    img = img.resize(((160, 160)))
    img = np.around(np.array(img) / 255.0, decimals=12)
    x_train = np.expand_dims(img, axis=0)
    embedding = model.predict_on_batch(x_train)
    return embedding / np.linalg.norm(embedding, ord=2)

def encode_List(customer_Image_List, customer_Name_List):
    encoded_Image_List = {}
    for i, image_path in enumerate(customer_Image_List):
        encoded_image = img_path_to_encoding(image_path,FRmodel)
        encoded_Image_List[customer_Name_List[i]] = encoded_image
    return encoded_Image_List

encoded_Database_List = encode_List(customer_Image_List, customer_Name_List)
print(encoded_Database_List.keys())

def checkoutCustomerRecognition(customer_Name):
    with open('output/CustomerCheckoutList.csv', 'r+') as f:
        customer_Data = f.readlines()
        customer_List = []
        for line in customer_Data:
            entry = line.split(',')
            customer_List.append(entry[0])
        if customer_Name not in customer_List:
            now = datetime.now()
            dtString = now.strftime('%H:%M:%S')
            f.writelines(f'\n{customer_Name},{dtString}')


webcam = cv2.VideoCapture(0)

while True:
    try:
        success, img = webcam.read()
        if not success:
            cv2.waitKey(1)
            continue
        encode_Current_Customer = img_to_encoding(img, FRmodel)
        min_dist, identity = who_is_it(encode_Current_Customer, encoded_Database_List, model)
        if min_dist < 0.65:
            cv2.putText(img, identity, (6, 60), cv2.FONT_HERSHEY_COMPLEX, 1, (0, 0, 0), 1)
            checkoutCustomerRecognition(identity)
        else:
            cv2.putText(img, "Please register on Portal", ( 6, 6), cv2.FONT_HERSHEY_COMPLEX, 1, (0, 0, 0), 1)
        cv2.imshow('CUSTOMER CHECKOUT', img)
        cv2.waitKey(1)
    except KeyboardInterrupt:
        print('Exiting program.')
        break


