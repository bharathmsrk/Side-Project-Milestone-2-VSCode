import pandas as pd 
import numpy as np

df = pd.read_csv("input/Grocery_Db.csv")
prices = np.round(np.absolute(np.random.randn(df.shape[0])*50),2)
# df.drop('id')
df["price"] = prices
for i in range(df.shape[0]):
    df.at[i,'name'] = df.at[i,'name'].replace(',',' ')
print (df)
df.to_csv('input/ModifiedGroceryList.csv')

print(df.shape)