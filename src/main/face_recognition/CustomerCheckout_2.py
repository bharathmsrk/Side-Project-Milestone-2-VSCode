import cv2
import face_recognition
import os
import numpy as np
from datetime import datetime, timedelta

path = '/Users/bharathradhakrishnan/PycharmProjects/pythonSideProject/ImageData'
customer_Image_List = []
customer_Name_List = []
list = os.listdir(path)
for name in list:
    a = cv2.imread(f'{path}/{name}')
    customer_Image_List.append(a)
    customer_Name_List.append(os.path.splitext(name)[0])


def encode_List(customer_Image_List):
    encoded_Image_List = []
    for image in customer_Image_List:
        image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        encoded_image = face_recognition.face_encodings(image)[0]
        encoded_Image_List.append(encoded_image)
    return encoded_Image_List

encoded_Database_List = encode_List(customer_Image_List)

def checkoutCustomerRecognition(customer_Name):
    with open('/Users/bharathradhakrishnan/side-project-bharathmsrk/output/CustomerCheckoutList.csv', 'r+') as f:
        customer_Data = f.readlines()
        customer_List = {}
        for line in customer_Data:
            entry = line.split(',')
            customer_List[entry[0]] = entry
        if customer_Name not in customer_List.keys():
            now = datetime.now()
            dtString = now.strftime('%d/%m/%Y %H:%M:%S')
            f.writelines(f'\n{customer_Name},{dtString}')
            return False
        else:
            if (datetime.strptime(customer_List[customer_Name][1],'%d/%m/%Y %H:%M:%S') + timedelta(minutes=1)) < datetime.now():
                return True
            else:
                return False

webcam = cv2.VideoCapture(0)

while True:
    try:
        success, img = webcam.read()
        current_Customer = face_recognition.face_locations(img)
        encode_Current_Customer = face_recognition.face_encodings(img, current_Customer)

        for encoded_Face, face_Location in zip(encode_Current_Customer, current_Customer):
            matches = face_recognition.compare_faces(encoded_Database_List, encoded_Face)
            face_Distance = face_recognition.face_distance(encoded_Database_List, encoded_Face)
            matchIndex = np.argmin(face_Distance)

            y1, x2, y2, x1 = face_Location
            cv2.rectangle(img, (x1, y1), (x2, y2), (153, 255, 204), 2)
            cv2.rectangle(img, (x1, y2 - 35), (x2, y2), (153, 255, 204), cv2.FILLED)
            if matches[matchIndex]:
                name = customer_Name_List[matchIndex]
                in_db = checkoutCustomerRecognition(name)
                if in_db:
                    cv2.putText(img, name+": Please Clear Dues First", (x1 + 6, y2 - 6), cv2.FONT_HERSHEY_COMPLEX, 1, (0, 0, 0), 1)
                else:
                    cv2.putText(img, name, (x1 + 6, y2 - 6), cv2.FONT_HERSHEY_COMPLEX, 1,(0, 0, 0), 1)

            else:
                cv2.putText(img, "Please register on Portal", (x1 + 6, y2 - 6), cv2.FONT_HERSHEY_COMPLEX, 1, (0, 0, 0), 1)

        cv2.imshow('CUSTOMER CHECKOUT', img)
        cv2.waitKey(1)
    except KeyboardInterrupt:
        print('Exiting program.')
        break


