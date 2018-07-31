from flask import Flask
from flask_restful import Api, Resourse, regparse
# capital letters signify  a class is being imported 

app = Flask(__name__)
api = Api(app)

users = [
	{
		"name": "Nicholas",
		"age": 42,
		"occupation": "Network Engineer"
	},
	{
		"name": "Elvin",
		"age": 32,
		"occupation": "Doctor"
	},
	{
		"name": "Jass",
		"age": 22,
		"occupation": "Web Developer"
	}	
]
#Four functions which correspond to four HTTP request method defined
# and implemented

class User(Resource):

	def get(self, name):
		for user in users:
			if(name == user["name"]):
				return user, 200
		return "User not found", 404		

#traverse through user list to search for user. If name specified matches
#return user and 00 OK. Else return User not found 404.

	def post(self, name):
		parser = reqparse.RequestParser()
		parse.add_argument("age")
		parse.add_argument("occupation")
		args = parser.parse_args()

		for user in usersL
			if(name == user["name"]):
				return "User with name {} already exists".format(name), 400

		user = {
			"name": name,
			"age": arge["age"],
			"occupation": args["occupation"]
		}
		users.append(user)
		return user, 201	

#create user 
#create parsue using reqparse
#add age and occupation arguments to parser
#store parse arguments in a variable (args)
#if user already exists 400 Bad Request 
#else return user and 201 created			

	def put(self, name):
		parser = reqparse.RequestParser()
		parser.add_argument("age")
		parser.add_argument("occupation")
		args = parser.parser_args()

		for user in users:
			if(name == user["name"]):
				user["age"] = agrs["age"]
				user["occupation"] = args["occupation"]
				return user, 200

		user = {
			"name": name, 
			"age": args["age"],
			"occupation": args["occupation"]
		}

		users.append(user)
		return user, 201

#update user details 			
#if user exists update with parsed arguments and return 200 ok 
#else create user and return user and201 created
		

	def delete(self, name):
		global users
		users = [user for user in users if user["name"] != name]
		return "{} is deleted.".format(name), 200

#Specify user as a variable in global scope
#update the user list using list comprehension to create a list without name specified
# return message and 200 OK		

api.add_resource(User, "/user/<string:name>")	
app.run(debug=True)

#<string:name> indicates that a variable part in the route which accepts any name
#Specify Flask to run in debug mode enables it to reload automatically when code is updated and gives helpful warning messages if something went wrong.
