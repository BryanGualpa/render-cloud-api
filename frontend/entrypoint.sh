#!/bin/sh
export PORT=${PORT:-80}

cat > /usr/share/nginx/html/config.js <<EOF
window.__API_URL__ = "${API_URL:-http://localhost:5000}";
EOF

sed -i "s/listen 80/listen ${PORT}/g" /etc/nginx/conf.d/default.conf

exec nginx -g 'daemon off;'
