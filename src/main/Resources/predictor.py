#!/usr/bin/env python
# coding: utf-8

import nltk
# tokenizing in various ways
from nltk.tokenize import word_tokenize
# stopwwords collection
from nltk.corpus import stopwords
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer

import pickle
# lemmatize like stem
from nltk.stem import WordNetLemmatizer
 
# To Wrap up the sklearn classifiers to be used in NLP for classifying
from nltk.classify.scikitlearn import SklearnClassifier

from sklearn.metrics import *
from sklearn.naive_bayes import MultinomialNB

import numpy as np
import pandas as pd
import sys
import os

#a = sys.stdin.read()
a = input()
if a == "":
    sys.exit()

class Preprocessor:

    def preprocessor(self,doc):
        lm = WordNetLemmatizer()
        preprop = lambda x: ' '.join([lm.lemmatize(word) for word in x.split() if word not in stopwords.words('english') and not(word.isalpha() or word.startswith('@') or word.isnumeric() or (word in ['!','.',','])) ])
        return doc.apply(preprop)


# path of model in local system
model_path = "C:\\Users\\Siddesh\\Downloads\\twiiter\\data.pkl"
# for jenkins deployed model
# model_path = "/home/ubuntu/tweeter_app/model.pkl"

file = open(model_path,'rb')
pre_process =  pickle.load(file)
cv = pickle.load(file)
mnb = pickle.load(file)
file.close()

st = "poor day,I'm doing bad"

z = a.split("||")

df = pd.DataFrame()

for each in z:
    df=df.append([each])

df.head()

new_df=pre_process.preprocessor(df[0])

new_df.head()

transfromed = cv.transform(new_df[0])

transfromed

pred = mnb.predict(transfromed)

pred_str = []

for i in range(0,len(pred)):
    if pred[i] == 0:
        pred_str.append("negetive")
    else:
        pred_str.append("positive")


# In[188]:


# for each in df.values:
#     print(each)


# In[186]:


df['pred'] = pred_str


# In[190]:


# df.head()


# In[161]:


for each in df.values:
    print(each)


# In[ ]:


#newcols = ["tweet",]


# In[ ]:


# df.name(columns=newcols, inplace=True)

