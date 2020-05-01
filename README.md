# Spark-ctr-models
CTR prediction models based on spark. Easy to use and we realized most common models for CTR prediction. The most difference is that we not only implement model trains，but also make it easy to parse the trained models and deploy them to the Online Server with Java interface.

The main algorithms we realized:

- [x] LR

- [x] FM

- [x] XGBoost

- [x] XGBoostLR

- [x] XGBoostFM

# 1. java
Java interfaces mainly used for parse and deploy the models trained by spark platform to the Online Server.

# 2. scala
Scala module trained models and save them as a specific format which java interface can parse them for predict in Online Server.

# 3. Feature Engineering.
- [x] FeatureConf:
- [x] FeatureMaker:
- [x] SparseVector:

# 4. Example code. 
In whole of this project, we will write a main function to show the example code of the detail usage of this class. This make you clear of it, and easy to test the functions of the class.