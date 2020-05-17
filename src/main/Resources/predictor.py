from nltk.corpus import stopwords
import pickle
from nltk.stem import WordNetLemmatizer
from sklearn.metrics import *
import pandas as pd
import sys

a = sys.stdin.read()
text= a
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

model_path = "C:\\Users\\Siddesh\\IdeaProjects\\TweeterApp\\src\\main\\Resources\\data.pkl"
file = open(model_path, 'rb')
pre_process = pickle.load(file)
cv = pickle.load(file)
mnb = pickle.load(file)
file.close()
df = pd.DataFrame()
av= pd.Series(a)
df = df.append(av,ignore_index=True)
transfromed = cv.transform(df[0])
transfromed
pred = mnb.predict(transfromed)
pred_str = []
text = text.replace("\n", " ")
text = text.replace(",", "")
text += ","
for i in range(0, len(pred)):
    if pred[i] == 0:
        text += " negative"
    else:
        text += " positive"
text += "\n"
filename = "C:\\Users\\Siddesh\\IdeaProjects\\TweeterApp\\src\\main\\Resources\\record.csv"
file1 = open(filename,"a")#append mode
file1.write(text)
file1.close()