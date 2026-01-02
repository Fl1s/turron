FROM nginx:latest

COPY ../client /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 3010

CMD ["nginx", "-g", "daemon off;"]