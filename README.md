

# Documento de Arquitectura del Sistema de Gestión de Órdenes y Entregas

## 1. Introducción  
Este documento describe la arquitectura inicial del sistema de gestión de órdenes y entregas, incluyendo requisitos funcionales, requisitos de calidad y restricciones que deben ser consideradas en el diseño del software.

**Equipo:** _[Desarrollo 3]_  
**Integrantes:** _[Juan Alejandro Jimenez Mendoza, Diego Alejandro Galvis Unas]_  
**Fecha:** _[12/03/2026]_  

---

## 2. Requisitos Funcionales  
Los requisitos funcionales se presentan en forma de **historias de usuario**, especificando los **criterios de aceptación**.

### **Historias de Usuario**
| **ID**    | **Historia de Usuario**         | **Criterios de Aceptación**         | INVEST                                      |
| --------- | ------------------------------- | ----------------------------------- | ------------------------------------------- |
| **US-01** | _[Como Organizador, quiero publicar un evento con cupos definidos para iniciar la preventa]_ | _[Debe permitir definir nombre, fecha, tipo de boleta (VIP, General) y cantidad disponible. 2. Debe permitir activar/desactivar códigos de descuento]_ | _[Cumple. Es independiente y negociable entre el organizador y la plataforma]_ |
| **US-02** | _[Como Cliente, quiero comprar una boleta pagando a través de una pasarela real para asegurar mi asistencia]_ | _[Integración con Sandbox de pagos real (callbacks/confirmaciones). 2. No permitir sobreventa (vender más que el aforo)]_ | _[Cumple. Valorable para el negocio y testeable en Sandbox]_ |                                           |
| **US-03** | _[Como Cliente, quiero recibir una boleta digital con QR tras confirmar mi pago para ingresar al evento]_ | _[1. Generación de QR único por boleta. 2. Envío de notificación por correo/mensaje tras confirmación]_ | _[Cumple. Pequeña y específica para el flujo de salida.]_                                           |
| **US-04** | _[Como Personal de Logística, quiero escanear el QR para validar la entrada de forma rápida]_ | _[Validación en tiempo real de boleta no usada previamente. 2. Respuesta eficiente incluso con señal de red regular]_ | _[Cumple. Estimable y crucial para la operación en sitio.]_ |
| **US-05** | _[Como Gerente, quiero ver métricas de ventas en tiempo real para tomar decisiones comerciales]_ | _[Dashboard con horas de mayor venta y puntos de caída en el embudo. 2. Reporte de fallas en pagos o ingresos]_ | _[Cumple. Independiente de la transaccionalidad operativa]_ |
| **US-06** | _[Como Administrador, quiero un registro de auditoría de cambios en precios y cupos para resolver reclamos]_ | _[Log inalterable de "quién, cuándo y por qué" se modificó un evento. 2. Trazabilidad completa del ciclo de vida de una orden]_ | _[Cumple. Esencial para el soporte técnico]_ |

>  **Instrucciones:**  
> - Completar al menos **6 historias de usuario**.  
> - Asegurar que cada historia tenga criterios de aceptación claros y verificables.  

---

## 3. Requisitos de Calidad  
Los requisitos de calidad se presentan en forma de **historias de calidad**, siguiendo la estructura de Len Bass.

### **Historias de Calidad**
| **ID**    | **Fuente**         | **Estímulo**         | **Artefactos**         | **Entorno**         | **Respuesta**         | **Medida de Respuesta**         |
| --------- | ------------------ | -------------------- | ---------------------- | ------------------- | --------------------- | ------------------------------- |
| **RQ-01** | _[Escalabilidad]_ | _[Pico de miles de usuarios al abrir ventas]_ | _[Servicio de Órdenes / DB]_ | _[Operación normal (lanzamiento)]_ | _[El sistema procesa las solicitudes sin caerse]_ | _[Soporte de concurrencia de miles de usuarios por minuto]_ |
| **RQ-02** | _[Disponibilidad]_ | _[Falla en el servicio de notificaciones]_ | _[Microservicio de Notificaciones]_ | _[Operación continua]_ | _[El proceso de compra debe continuar aunque falle el aviso]_ | _[100% de las ventas finalizan exitosamente a pesar de la caída de servicios externos]_ |
| **RQ-03** | _[Seguridad]_ | _[Intento de acceso a funciones de admin por usuario regular]_ | _[API Gateway / Auth Service]_ | _[Producción]_ | _[El sistema bloquea el acceso basado en roles y permisos]_ | _[0 accesos no autorizados registrados en auditoría]_ |
| **RQ-04** | _[Consistencia]_ | _[Pago en estado "pendiente" o intermedio]_ | _[Servicio de Pagos]_ | _[Integración con Pasarela]_ | _[El sistema espera confirmación (callback) antes de emitir boleta]_ | _[0 boletas emitidas sin confirmación real de pago]_ |
| **RQ-05** | _[Rendimiento]_ | _[Validación de QR en puerta con baja señal]_ | _[Servicio de Validación]_ | _[Evento en vivo]_ | _[Respuesta rápida para evitar congestión en filas]_ | _[Tiempo de validación < 2 segundos]_ |
| **RQ-06** | _[Modificabilidad]_ | _[Crecimiento de 2 a 10 ciudades]_ | _[Código fuente / Infraestructura]_ | _[Evolución del negocio]_ | _[Se pueden agregar nuevos eventos/ciudades sin rehacer el sistema]_ | _[Adición de nuevos módulos o servicios sin afectar los existentes]_ |

>  **Instrucciones:**  
> - Completar al menos **6 historias de calidad**, alineadas con atributos clave como **rendimiento, escalabilidad y seguridad**.  
> - Definir cómo se medirá la respuesta esperada ante la situación planteada.  

---

## 4. Restricciones del Sistema  
Las restricciones establecen **limitaciones** en la arquitectura del sistema, ya sean tecnológicas, de negocio, regulatorias o de infraestructura.

### **Lista de Restricciones**
| **Tipo de Restricción** | **Descripción**                                                                                                                                                          |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| _[Tecnológica]_   | _[Arquitectura de Microservicios con bases de datos independientes por servicio (no compartir DB)]_ |
| _[Operativa]_ | _[Uso de contenedores (Docker) para facilitar el despliegue y las pruebas]_ |
| _[De Negocio]_ | _[Entrega del MVP funcional en un plazo máximo de 13 semanas]_ |
| _[Seguridad]_ | _[Prohibido exponer llaves, tokens o secretos en el código fuente]_ |
| _[Infraestructura]_ | _[El sistema debe ser levantable y repetible mediante el repositorio (scripts de despliegue)]_ |





