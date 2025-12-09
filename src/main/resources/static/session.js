(function() {
    'use strict';

    const SESSION_TIMEOUT = 10 * 60 * 1000;
    const WARNING_TIME = 1 * 60 * 1000;
    const CHECK_INTERVAL = 1000;
    const SERVER_CHECK_INTERVAL = 10000;
    let lastActivityTime = Date.now();
    let warningShown = false;
    let checkIntervalId = null;
    let serverCheckIntervalId = null;
    let warningDialog = null;
    let isLoggingOut = false;

    const activityEvents = [
        'mousedown',
        'mousemove',
        'keypress',
        'scroll',
        'touchstart',
        'click'
    ];

    function hasSessionActiveCookie() {
        return document.cookie
            .split(';')
            .some(item => item.trim().startsWith('SESSION_ACTIVE='));
    }

    function updateActivity() {
        lastActivityTime = Date.now();
        if (warningShown) {
            closeWarningDialog();
        }
    }

    function createWarningDialog() {
        const overlay = document.createElement('div');
        overlay.id = 'session-warning-overlay';
        overlay.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.6);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 99999;
            backdrop-filter: blur(3px);
        `;
        const dialog = document.createElement('div');
        dialog.style.cssText = `
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
            max-width: 450px;
            text-align: center;
            animation: slideIn 0.3s ease-out;
        `;
        const style = document.createElement('style');
        style.textContent = `
            @keyframes slideIn {
                from {
                    transform: translateY(-50px);
                    opacity: 0;
                }
                to {
                    transform: translateY(0);
                    opacity: 1;
                }
            }
        `;
        document.head.appendChild(style);
        const icon = document.createElement('div');
        icon.innerHTML = `
            <svg width="60" height="60" viewBox="0 0 24 24" fill="none" style="margin: 0 auto 15px;">
                <circle cx="12" cy="12" r="10" stroke="#f57c00" stroke-width="2"/>
                <path d="M12 8v4M12 16h.01" stroke="#f57c00" stroke-width="2" stroke-linecap="round"/>
            </svg>
        `;
        const title = document.createElement('h3');
        title.textContent = 'Phiên làm việc sắp hết hạn';
        title.style.cssText = `
            margin: 0 0 10px;
            color: #333;
            font-size: 20px;
            font-weight: 600;
        `;
        const message = document.createElement('p');
        message.id = 'session-warning-message';
        message.style.cssText = `
            color: #666;
            margin: 0 0 25px;
            font-size: 15px;
            line-height: 1.5;
        `;
        const buttonContainer = document.createElement('div');
        buttonContainer.style.cssText = `
            display: flex;
            gap: 10px;
            justify-content: center;
        `;
        const continueBtn = document.createElement('button');
        continueBtn.textContent = 'Tiếp tục làm việc';
        continueBtn.style.cssText = `
            background: linear-gradient(135deg, #f57c00, #ff9800);
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            transition: transform 0.2s, box-shadow 0.2s;
        `;
        continueBtn.onmouseover = () => {
            continueBtn.style.transform = 'translateY(-2px)';
            continueBtn.style.boxShadow = '0 5px 15px rgba(245, 124, 0, 0.3)';
        };
        continueBtn.onmouseout = () => {
            continueBtn.style.transform = 'translateY(0)';
            continueBtn.style.boxShadow = 'none';
        };
        continueBtn.onclick = () => {
            updateActivity();
            closeWarningDialog();
            sendHeartbeat();
        };
        const logoutBtn = document.createElement('button');
        logoutBtn.textContent = 'Đăng xuất';
        logoutBtn.style.cssText = `
            background: #fff;
            color: #666;
            border: 2px solid #ddd;
            padding: 12px 24px;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            transition: all 0.2s;
        `;
        logoutBtn.onmouseover = () => {
            logoutBtn.style.borderColor = '#999';
            logoutBtn.style.color = '#333';
        };
        logoutBtn.onmouseout = () => {
            logoutBtn.style.borderColor = '#ddd';
            logoutBtn.style.color = '#666';
        };
        logoutBtn.onclick = () => {
            logout();
        };
        buttonContainer.appendChild(continueBtn);
        buttonContainer.appendChild(logoutBtn);
        dialog.appendChild(icon);
        dialog.appendChild(title);
        dialog.appendChild(message);
        dialog.appendChild(buttonContainer);
        overlay.appendChild(dialog);
        return overlay;
    }

    function showWarning(remainingSeconds) {
        if (!warningShown) {
            warningDialog = createWarningDialog();
            document.body.appendChild(warningDialog);
            warningShown = true;
        }
        const message = document.getElementById('session-warning-message');
        if (message) {
            message.textContent =
                `Bạn chưa có hoạt động trong 9 phút. ` +
                `Phiên làm việc sẽ tự động đăng xuất sau ${remainingSeconds} giây nếu không có hoạt động.`;
        }
    }

    function closeWarningDialog() {
        if (warningDialog && warningDialog.parentNode) {
            warningDialog.remove();
            warningDialog = null;
            warningShown = false;
        }
    }

    function logout() {
        if (isLoggingOut) return;
        isLoggingOut = true;
        stopMonitoring();
        document.cookie = 'JWT_TOKEN=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
        document.cookie = 'SESSION_ACTIVE=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
        showLogoutMessage();
        fetch('/logout', {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            }
        })
            .then(() => {
                console.log('Server logout successful');
                setTimeout(() => {
                    window.location.href = '/login';
                }, 2000);
            })
            .catch(error => {
                console.error('Logout failed:', error);
                setTimeout(() => {
                    window.location.href = '/login';
                }, 2000);
            });
    }

    function showLogoutMessage() {
        closeWarningDialog();
        const overlay = document.createElement('div');
        overlay.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.6);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 99999;
            backdrop-filter: blur(3px);
        `;
        const message = document.createElement('div');
        message.style.cssText = `
            background: white;
            padding: 30px 40px;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
            text-align: center;
            animation: fadeIn 0.3s ease-out;
        `;
        message.innerHTML = `
            <svg width="50" height="50" viewBox="0 0 24 24" fill="none" style="margin: 0 auto 15px;">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9"
                      stroke="#f57c00" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <h3 style="margin: 0 0 10px; color: #333; font-size: 18px;">Phiên làm việc đã hết hạn</h3>
            <p style="color: #666; margin: 0; font-size: 14px;">Đang đăng xuất khỏi hệ thống...</p>
        `;
        const style = document.createElement('style');
        style.textContent = `
            @keyframes fadeIn {
                from { opacity: 0; transform: scale(0.9); }
                to { opacity: 1; transform: scale(1); }
            }
        `;
        document.head.appendChild(style);
        overlay.appendChild(message);
        document.body.appendChild(overlay);
    }

    function checkSessionTimeout() {
        const now = Date.now();
        const inactiveTime = now - lastActivityTime;
        const remainingTime = SESSION_TIMEOUT - inactiveTime;
        if (remainingTime <= 0) {
            console.log('Session timeout - logging out');
            logout();
        } else if (inactiveTime >= (SESSION_TIMEOUT - WARNING_TIME) && !warningShown) {
            const remainingSeconds = Math.ceil(remainingTime / 1000);
            showWarning(remainingSeconds);
        } else if (warningShown && inactiveTime < (SESSION_TIMEOUT - WARNING_TIME)) {
            closeWarningDialog();
        }
        if (warningShown && remainingTime > 0) {
            const remainingSeconds = Math.ceil(remainingTime / 1000);
            const message = document.getElementById('session-warning-message');
            if (message) {
                message.textContent =
                    `Bạn chưa có hoạt động trong 9 phút. ` +
                    `Phiên làm việc sẽ tự động đăng xuất sau ${remainingSeconds} giây nếu không có hoạt động.`;
            }
        }
    }

    function sendHeartbeat() {
        if (isLoggingOut) return;
        fetch(window.location.pathname, {
            method: 'HEAD',
            credentials: 'same-origin',
            cache: 'no-cache'
        })
            .then(response => {
                if (
                    response.status === 401 ||
                    response.status === 403 ||
                    response.headers.get('X-Session-Expired') === 'true'
                ) {
                    console.log('Server reported session expired (heartbeat)');
                    logout();
                }
            })
            .catch(error => {
                console.error('Heartbeat failed:', error);
            });
    }

    function setupAjaxInterceptor() {
        const originalFetch = window.fetch;
        window.fetch = function(...args) {
            return originalFetch.apply(this, args)
                .then(response => {
                    if (
                        response.status === 401 ||
                        response.status === 403 ||
                        response.headers.get('X-Session-Expired') === 'true'
                    ) {
                        console.log('Session expired detected in fetch');
                        if (!isLoggingOut) {
                            logout();
                        }
                    }
                    return response;
                })
                .catch(error => {
                    throw error;
                });
        };
        const originalOpen = XMLHttpRequest.prototype.open;
        XMLHttpRequest.prototype.open = function() {
            this.addEventListener('load', function() {
                const expiredHeader = this.getResponseHeader('X-Session-Expired');
                if (
                    this.status === 401 ||
                    this.status === 403 ||
                    expiredHeader === 'true'
                ) {
                    console.log('Session expired detected in XHR');
                    if (!isLoggingOut) {
                        logout();
                    }
                }
            });
            originalOpen.apply(this, arguments);
        };
    }

    function checkCookieExists() {
        const hasSession = hasSessionActiveCookie();
        if (!hasSession && !isLoggingOut) {
            logout();
        }
        return hasSession;
    }

    function startMonitoring() {
        activityEvents.forEach(event => {
            document.addEventListener(event, updateActivity, true);
        });
        checkIntervalId = setInterval(checkSessionTimeout, CHECK_INTERVAL);
        serverCheckIntervalId = setInterval(() => {
            checkCookieExists();
            sendHeartbeat();
        }, SERVER_CHECK_INTERVAL);
        setupAjaxInterceptor();
    }

    function stopMonitoring() {
        if (checkIntervalId) {
            clearInterval(checkIntervalId);
            checkIntervalId = null;
        }
        if (serverCheckIntervalId) {
            clearInterval(serverCheckIntervalId);
            serverCheckIntervalId = null;
        }
        activityEvents.forEach(event => {
            document.removeEventListener(event, updateActivity, true);
        });
    }

    document.addEventListener('visibilitychange', function() {
        if (!document.hidden && hasSessionActiveCookie()) {
            checkCookieExists();
            sendHeartbeat();
        }
    });

    function init() {
        if (hasSessionActiveCookie()) {
            startMonitoring();
            setTimeout(() => {
                checkCookieExists();
                sendHeartbeat();
            }, 1000);
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    window.sessionManager = {
        start: startMonitoring,
        stop: stopMonitoring,
        updateActivity: updateActivity,
        checkSession: function() {
            checkCookieExists();
            sendHeartbeat();
        }
    };
})();