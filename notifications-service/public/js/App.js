// Notifications Management App
import { createApp, ref, onMounted, onUnmounted, computed } from 'vue';

export default {
    setup() {
        // State
        const notifications = ref([]);
        const stats = ref({});
        const loading = ref(false);
        const error = ref('');
        const filterType = ref('all');
        const filterSeverity = ref('all');
        const showUnreadOnly = ref(false);
        const autoRefresh = ref(true);
        const refreshInterval = ref(null);
        const lastUpdate = ref(null);
        const soundEnabled = ref(true);

        // API Base URL
        const API_BASE = '/api/notifications';

        // Computed properties
        const filteredNotifications = computed(() => {
            let filtered = notifications.value;

            if (showUnreadOnly.value) {
                filtered = filtered.filter(n => !n.read);
            }

            if (filterType.value !== 'all') {
                filtered = filtered.filter(n => n.type === filterType.value);
            }

            if (filterSeverity.value !== 'all') {
                filtered = filtered.filter(n => n.severity === filterSeverity.value);
            }

            return filtered;
        });

        const unreadCount = computed(() => {
            return notifications.value.filter(n => !n.read).length;
        });

        const notificationTypes = computed(() => {
            const types = [...new Set(notifications.value.map(n => n.type))];
            return types.sort();
        });

        const severityTypes = computed(() => {
            const severities = [...new Set(notifications.value.map(n => n.severity))];
            return severities.sort();
        });

        // API Functions
        const fetchNotifications = async () => {
            try {
                loading.value = true;
                error.value = '';
                
                const response = await fetch(`${API_BASE}/recent?limit=100`);
                if (!response.ok) throw new Error('Failed to fetch notifications');
                
                const data = await response.json();
                
                // Check for new notifications to play sound
                if (soundEnabled.value && notifications.value.length > 0) {
                    const newNotifications = data.filter(n => 
                        !notifications.value.some(existing => existing.id === n.id)
                    );
                    if (newNotifications.length > 0) {
                        playNotificationSound();
                    }
                }
                
                notifications.value = data;
                lastUpdate.value = new Date();
            } catch (err) {
                error.value = 'Error fetching notifications: ' + err.message;
                console.error('Error fetching notifications:', err);
            } finally {
                loading.value = false;
            }
        };

        const fetchStats = async () => {
            try {
                const response = await fetch(`${API_BASE}/stats`);
                if (!response.ok) throw new Error('Failed to fetch stats');
                
                stats.value = await response.json();
            } catch (err) {
                console.error('Error fetching stats:', err);
            }
        };

        const markAsRead = async (id) => {
            try {
                const response = await fetch(`${API_BASE}/${id}/read`, {
                    method: 'PUT'
                });
                if (!response.ok) throw new Error('Failed to mark as read');
                
                // Update local state
                const notification = notifications.value.find(n => n.id === id);
                if (notification) {
                    notification.read = true;
                }
                
                await fetchStats(); // Update stats
            } catch (err) {
                error.value = 'Error marking notification as read: ' + err.message;
                console.error('Error marking as read:', err);
            }
        };

        const markAllAsRead = async () => {
            try {
                const response = await fetch(`${API_BASE}/read-all`, {
                    method: 'PUT'
                });
                if (!response.ok) throw new Error('Failed to mark all as read');
                
                // Update local state
                notifications.value.forEach(n => n.read = true);
                
                await fetchStats(); // Update stats
            } catch (err) {
                error.value = 'Error marking all as read: ' + err.message;
                console.error('Error marking all as read:', err);
            }
        };

        const createTestNotification = async () => {
            try {
                const types = ['ORDER_CREATED', 'ORDER_CANCELLED', 'LOW_STOCK', 'PAYMENT_CONFIRMED', 'TEST'];
                const severities = ['SUCCESS', 'INFO', 'WARNING', 'ERROR'];
                
                const randomType = types[Math.floor(Math.random() * types.length)];
                const randomSeverity = severities[Math.floor(Math.random() * severities.length)];
                
                const response = await fetch(`${API_BASE}/test`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({
                        type: randomType,
                        title: `Test ${randomType}`,
                        message: `This is a test notification of type ${randomType}`,
                        severity: randomSeverity
                    })
                });
                
                if (!response.ok) throw new Error('Failed to create test notification');
                
                // Refresh notifications
                setTimeout(fetchNotifications, 500);
            } catch (err) {
                error.value = 'Error creating test notification: ' + err.message;
                console.error('Error creating test notification:', err);
            }
        };

        // Utility functions
        const formatTimestamp = (timestamp) => {
            return new Date(timestamp).toLocaleString();
        };

        const getRelativeTime = (timestamp) => {
            const now = new Date();
            const time = new Date(timestamp);
            const diffMs = now - time;
            const diffMins = Math.floor(diffMs / 60000);
            const diffHours = Math.floor(diffMs / 3600000);
            const diffDays = Math.floor(diffMs / 86400000);

            if (diffMins < 1) return 'Just now';
            if (diffMins < 60) return `${diffMins}m ago`;
            if (diffHours < 24) return `${diffHours}h ago`;
            if (diffDays < 7) return `${diffDays}d ago`;
            return formatTimestamp(timestamp);
        };

        const getSeverityClass = (severity) => {
            const classes = {
                'SUCCESS': 'severity-success',
                'INFO': 'severity-info',
                'WARNING': 'severity-warning',
                'ERROR': 'severity-error'
            };
            return classes[severity] || 'severity-info';
        };

        const getSeverityIcon = (severity) => {
            const icons = {
                'SUCCESS': '✅',
                'INFO': 'ℹ️',
                'WARNING': '⚠️',
                'ERROR': '❌'
            };
            return icons[severity] || 'ℹ️';
        };

        const getTypeIcon = (type) => {
            const icons = {
                'ORDER_CREATED': '📦',
                'ORDER_CANCELLED': '❌',
                'LOW_STOCK': '📉',
                'PAYMENT_CONFIRMED': '💰',
                'DIRECT': '📧',
                'TEST': '🧪'
            };
            return icons[type] || '📢';
        };

        const playNotificationSound = () => {
            if (soundEnabled.value) {
                // Create a simple beep sound
                const audioContext = new (window.AudioContext || window.webkitAudioContext)();
                const oscillator = audioContext.createOscillator();
                const gainNode = audioContext.createGain();
                
                oscillator.connect(gainNode);
                gainNode.connect(audioContext.destination);
                
                oscillator.frequency.value = 800;
                oscillator.type = 'sine';
                
                gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
                gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.1);
                
                oscillator.start(audioContext.currentTime);
                oscillator.stop(audioContext.currentTime + 0.1);
            }
        };

        const startAutoRefresh = () => {
            if (autoRefresh.value && !refreshInterval.value) {
                refreshInterval.value = setInterval(() => {
                    fetchNotifications();
                    fetchStats();
                }, 3000); // Refresh every 3 seconds
            }
        };

        const stopAutoRefresh = () => {
            if (refreshInterval.value) {
                clearInterval(refreshInterval.value);
                refreshInterval.value = null;
            }
        };

        const toggleAutoRefresh = () => {
            autoRefresh.value = !autoRefresh.value;
            if (autoRefresh.value) {
                startAutoRefresh();
            } else {
                stopAutoRefresh();
            }
        };

        const refreshData = async () => {
            await Promise.all([fetchNotifications(), fetchStats()]);
        };

        const clearFilters = () => {
            filterType.value = 'all';
            filterSeverity.value = 'all';
            showUnreadOnly.value = false;
        };

        // Lifecycle
        onMounted(async () => {
            await refreshData();
            startAutoRefresh();
        });

        onUnmounted(() => {
            stopAutoRefresh();
        });

        return {
            // State
            notifications,
            stats,
            loading,
            error,
            filterType,
            filterSeverity,
            showUnreadOnly,
            autoRefresh,
            lastUpdate,
            soundEnabled,
            
            // Computed
            filteredNotifications,
            unreadCount,
            notificationTypes,
            severityTypes,
            
            // Functions
            fetchNotifications,
            markAsRead,
            markAllAsRead,
            createTestNotification,
            formatTimestamp,
            getRelativeTime,
            getSeverityClass,
            getSeverityIcon,
            getTypeIcon,
            toggleAutoRefresh,
            refreshData,
            clearFilters
        };
    },

    template: `
        <div class="notifications-app">
            <header class="app-header">
                <div class="header-content">
                    <h1>
                        📢 Notifications Center
                        <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }}</span>
                    </h1>
                    <div class="header-controls">
                        <label class="toggle-switch">
                            <input type="checkbox" v-model="autoRefresh" @change="toggleAutoRefresh">
                            <span class="slider"></span>
                            <span class="label">Auto Refresh</span>
                        </label>
                        <label class="toggle-switch">
                            <input type="checkbox" v-model="soundEnabled">
                            <span class="slider"></span>
                            <span class="label">🔊 Sound</span>
                        </label>
                        <button @click="refreshData" class="btn btn-refresh" :disabled="loading">
                            🔄 Refresh
                        </button>
                    </div>
                </div>
            </header>

            <div v-if="error" class="error-message">
                {{ error }}
                <button @click="error = ''" class="close-btn">×</button>
            </div>

            <div class="controls-section">
                <div class="filters">
                    <div class="filter-group">
                        <label>Type:</label>
                        <select v-model="filterType">
                            <option value="all">All Types</option>
                            <option v-for="type in notificationTypes" :key="type" :value="type">
                                {{ getTypeIcon(type) }} {{ type }}
                            </option>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label>Severity:</label>
                        <select v-model="filterSeverity">
                            <option value="all">All Severities</option>
                            <option v-for="severity in severityTypes" :key="severity" :value="severity">
                                {{ getSeverityIcon(severity) }} {{ severity }}
                            </option>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label class="checkbox-label">
                            <input type="checkbox" v-model="showUnreadOnly">
                            Unread only
                        </label>
                    </div>

                    <button @click="clearFilters" class="btn btn-clear">Clear Filters</button>
                </div>

                <div class="actions">
                    <button @click="markAllAsRead" class="btn btn-mark-all" :disabled="unreadCount === 0">
                        Mark All Read ({{ unreadCount }})
                    </button>
                    <button @click="createTestNotification" class="btn btn-test">
                        Create Test Notification
                    </button>
                </div>
            </div>

            <div class="stats-section" v-if="stats.total">
                <div class="stat-card">
                    <h3>📊 Statistics</h3>
                    <div class="stat-grid">
                        <div class="stat-item">
                            <span class="stat-value">{{ stats.total }}</span>
                            <span class="stat-label">Total</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-value">{{ stats.unread }}</span>
                            <span class="stat-label">Unread</span>
                        </div>
                        <div class="stat-item" v-for="(count, type) in stats.byType" :key="type">
                            <span class="stat-value">{{ count }}</span>
                            <span class="stat-label">{{ getTypeIcon(type) }} {{ type }}</span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="notifications-section">
                <div class="section-header">
                    <h2>Recent Notifications ({{ filteredNotifications.length }})</h2>
                    <span v-if="lastUpdate" class="last-update">
                        Last updated: {{ formatTimestamp(lastUpdate) }}
                    </span>
                </div>

                <div v-if="loading" class="loading">
                    Loading notifications...
                </div>

                <div v-else-if="filteredNotifications.length === 0" class="empty-state">
                    <div class="empty-icon">📭</div>
                    <p>No notifications found</p>
                    <button @click="createTestNotification" class="btn btn-test">
                        Create Test Notification
                    </button>
                </div>

                <div v-else class="notifications-list">
                    <div 
                        v-for="notification in filteredNotifications" 
                        :key="notification.id"
                        class="notification-card"
                        :class="[
                            getSeverityClass(notification.severity),
                            { 'unread': !notification.read }
                        ]"
                    >
                        <div class="notification-header">
                            <div class="notification-meta">
                                <span class="type-icon">{{ getTypeIcon(notification.type) }}</span>
                                <span class="severity-icon">{{ getSeverityIcon(notification.severity) }}</span>
                                <strong class="title">{{ notification.title }}</strong>
                                <span class="type-badge">{{ notification.type }}</span>
                            </div>
                            <div class="notification-actions">
                                <span class="timestamp">{{ getRelativeTime(notification.timestamp) }}</span>
                                <button 
                                    v-if="!notification.read"
                                    @click="markAsRead(notification.id)"
                                    class="btn btn-small btn-mark-read"
                                    title="Mark as read"
                                >
                                    ✓
                                </button>
                            </div>
                        </div>
                        
                        <div class="notification-content">
                            <p class="message">{{ notification.message }}</p>
                            <div class="notification-footer">
                                <span class="full-timestamp">{{ formatTimestamp(notification.timestamp) }}</span>
                                <span v-if="notification.read" class="read-status">✓ Read</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `
};