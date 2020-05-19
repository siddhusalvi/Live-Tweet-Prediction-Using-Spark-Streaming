train_data = "C:\\Users\\Siddesh\\Downloads\\twiiter\\data.csv"
model_path = "C:\\Users\\Siddesh\\Downloads\\twiiter\\data.pkl"

train_df =pd.read_csv(train_data, encoding='utf-8')
train_df.head()
train_df=train_df[["label","tweet"]]
train_df.head(3)
class Preprocessor:

    def preprocessor(self,doc):
        lm = WordNetLemmatizer()
        preprop = lambda x: ' '.join([lm.lemmatize(word) for word in x.split() if word not in stopwords.words('english') and not(word.isalpha() or word.startswith('@') or word.isnumeric() or (word in ['!','.',','])) ])
        return doc.apply(preprop)

pre_process = Preprocessor()
train_df= train_df.dropna()
train_df.isnull().sum()
train_df = train_df.drop_duplicates()
train_df = train_df.drop(train_df[(train_df["tweet"]=="&quot")].index.values, axis=0)
train_df.shape[0]
class by_count_vectorizer:
    
    def check_result(self,feature,classifier):
        if(classifier.predict(feature)==1):
            print('Good')
        else:
            print('Hate')
    
    def accuracy(Y_actual,Y_pred):
        correct = (Y_actual==Y_pred).sum()
        return (correct/Y_actual.shape[0])*100
    
    def classify_demo(self,train_df):
        pre_process = Preprocessor()
        train_df["tweet"] = pre_process.preprocessor(train_df["tweet"])
        cv = CountVectorizer()
        X_train,Y_train = train_df["tweet"],train_df["label"]
        X_train,X_cross,Y_train,Y_cross = train_test_split(X_train,Y_train,test_size=0.1)
        X_train = cv.fit_transform(X_train)
#         print("to array:\n",X_train.toarray())
        mnb = MultinomialNB()
        mnb.fit(X_train,Y_train)
        y_pred = mnb.predict(cv.transform(X_cross))
        print("Accuracy: ",by_count_vectorizer.accuracy(Y_cross,y_pred))
        file = open(model_path,'wb')
        pickle.dump(pre_process,file)
        pickle.dump(cv,file)
        pickle.dump(mnb,file)
        file.close()
        try:
            sys.stdout.write(model_path)
        except Exception as e:
            print("Cannot return the path beacause ",e)

cv_classifier = by_count_vectorizer()
cv_classifier.classify_demo(train_df)
