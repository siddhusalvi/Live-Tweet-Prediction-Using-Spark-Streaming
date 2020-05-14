# model_path = "C:\\Users\\Siddesh\\IdeaProjects\\TweeterApp\\src\\main\\Resources\\data.pkl"

from nltk.corpus import stopwords
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

a = sys.stdin.read()

if a == "":
    sys.exit()

ab = a.split(" ")
for i in ab:
    if not i.isalpha() or i.startswith("@") or i.startswith("#"):
        ab.remove(i)
a = " ".join(ab)



class Preprocessor:

    def preprocessor(self, doc):
        lm = WordNetLemmatizer()
        preprop = lambda x: ' '.join([lm.lemmatize(word) for word in x.split() if
                                      word not in stopwords.words('english') and not (
                                              word.isalpha() or word.startswith('@') or word.isnumeric() or (
                                              word in ['!', '.', ',']))])
        return doc.apply(preprop)


# path of model in local system
model_path = "C:\\Users\\Siddesh\\Downloads\\twiiter\\data.pkl"
# for jenkins deployed model
# model_path = "/home/ubuntu/tweeter_app/model.pkl"

file = open(model_path, 'rb')
pre_process = pickle.load(file)
cv = pickle.load(file)
mnb = pickle.load(file)
file.close()


# z = a.split("\n")
# z = ["I wasn't aware of the two versions of the #EU do","Im Gesprch: Das Corona-Regime wird fallen "]
df = pd.DataFrame()
av= pd.Series(a)
# for each in z:
#     df = df.append([each])
df = df.append(av,ignore_index=True)


# new_df = pre_process.preprocessor(df[0])
#
# new_df.head()

transfromed = cv.transform(df[0])

transfromed

pred = mnb.predict(transfromed)

pred_str = []

for i in range(0, len(pred)):
    if pred[i] == 0:
        pred_str.append("negetive")
    else:
        pred_str.append("positive")


df['pred'] = pred_str


for each in df.values:
    print(each)
