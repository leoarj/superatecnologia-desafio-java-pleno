FROM nginx:1.29.3-alpine

# Remove a configuração padrão do Nginx
RUN rm /etc/nginx/conf.d/default.conf

# Copia a nossa configuração customizada
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80