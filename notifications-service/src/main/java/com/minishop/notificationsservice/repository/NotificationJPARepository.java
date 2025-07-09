package com.minishop.notificationsservice.repository;

import com.minishop.notificationsservice.model.NotificationJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository JPA - Alternativa a tu NotificationRepository actual
 * 
 * Esta interfaz extiende JpaRepository y Spring Data automáticamente
 * genera la implementación con métodos CRUD + consultas personalizadas
 */
@Repository
public interface NotificationJPARepository extends JpaRepository<NotificationJPA, Long> {
    
    // ========================
    // MÉTODOS AUTOMÁTICOS por convención de nombres
    // Spring Data genera automáticamente la implementación
    // ========================
    
    /**
     * Buscar por tipo - Método automático
     * SQL generado: SELECT * FROM notifications WHERE notification_type = ?
     */
    List<NotificationJPA> findByType(String type);
    
    /**
     * Buscar no leídas - Método automático  
     * SQL generado: SELECT * FROM notifications WHERE is_read = false
     */
    List<NotificationJPA> findByReadFalse();
    
    /**
     * Buscar desde una fecha - Método automático
     * SQL generado: SELECT * FROM notifications WHERE created_at > ?
     */
    List<NotificationJPA> findByTimestampAfter(LocalDateTime timestamp);
    
    /**
     * Buscar por severidad - Método automático
     * SQL generado: SELECT * FROM notifications WHERE severity = ?
     */
    List<NotificationJPA> findBySeverity(String severity);
    
    /**
     * Top N más recientes - Método automático
     * SQL generado: SELECT * FROM notifications ORDER BY created_at DESC LIMIT ?
     */
    List<NotificationJPA> findTop50ByOrderByTimestampDesc();
    
    /**
     * Contar no leídas - Método automático
     * SQL generado: SELECT COUNT(*) FROM notifications WHERE is_read = false
     */
    long countByReadFalse();
    
    // ========================
    // CONSULTAS PERSONALIZADAS con @Query
    // ========================
    
    /**
     * Obtener todas ordenadas por fecha descendente
     */
    @Query("SELECT n FROM NotificationJPA n ORDER BY n.timestamp DESC")
    List<NotificationJPA> findAllOrderByTimestampDesc();
    
    /**
     * Obtener notificaciones recientes con límite
     */
    @Query("SELECT n FROM NotificationJPA n WHERE n.timestamp >= :since ORDER BY n.timestamp DESC")
    List<NotificationJPA> findRecentNotifications(@Param("since") LocalDateTime since);
    
    /**
     * Estadísticas por tipo usando SQL nativo
     */
    @Query(value = "SELECT notification_type, COUNT(*) as count FROM notifications GROUP BY notification_type", 
           nativeQuery = true)
    List<Object[]> getNotificationCountByType();
    
    /**
     * Estadísticas por severidad
     */
    @Query("SELECT n.severity, COUNT(n) FROM NotificationJPA n GROUP BY n.severity")
    List<Object[]> getNotificationCountBySeverity();
    
    /**
     * Limpiar notificaciones antiguas
     */
    @Query("DELETE FROM NotificationJPA n WHERE n.timestamp < :cutoffDate")
    void deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Marcar todas como leídas
     */
    @Query("UPDATE NotificationJPA n SET n.read = true WHERE n.read = false")
    int markAllAsRead();
}
