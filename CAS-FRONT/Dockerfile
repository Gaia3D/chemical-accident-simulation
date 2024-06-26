FROM nginx

WORKDIR /usr/app

#COPY package*.json ./
COPY ./dist /usr/share/nginx/html
#COPY . .

EXPOSE 80/tcp
CMD ["nginx", "-g", "daemon off;"]