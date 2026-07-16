#!/bin/sh
export PORT=${PORT:-80}

API_BACKEND=${API_BACKEND:-https://render-cloud-api-tyio.onrender.com}
API_HOST=$(echo "$API_BACKEND" | sed -E 's#https?://##')

cat > /usr/share/nginx/html/config.js <<EOF
window.__API_URL__ = "";
EOF

sed -e "s|__API_BACKEND__|${API_BACKEND}|g" \
    -e "s|__API_HOST__|${API_HOST}|g" \
    -e "s|listen 80|listen ${PORT}|g" \
    /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf

exec nginx -g 'daemon off;'
