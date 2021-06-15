#FROM node:10-alpine
#WORKDIR /app
## add `/app/node_modules/.bin` to $PATH
#ENV PATH /app/node_modules/.bin:$PATH
## install app dependencies
#COPY package.json ./
#COPY package-lock.json ./
#RUN npm install --silent
#RUN npm install react-scripts@3.4.1 -g --silent
#COPY . ./
#EXPOSE 8080
#CMD ["npm", "start"]

FROM node:10

WORKDIR /usr/src/app

COPY package.json ./

RUN npm install

COPY . .

EXPOSE 8081

CMD ["npm", "run", "serve"]

