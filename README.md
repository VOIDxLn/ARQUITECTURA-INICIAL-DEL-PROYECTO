

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
| **US-01** | Como Organizador, quiero publicar un evento con cupos definidos para iniciar la preventa | Debe permitir definir nombre, fecha, tipo de boleta (VIP, General) y cantidad disponible. 2. Debe permitir activar/desactivar códigos de descuento | Cumple. Es independiente y negociable entre el organizador y la plataforma |
| **US-02** | Como Cliente, quiero comprar una boleta pagando a través de una pasarela real para asegurar mi asistencia | Integración con Sandbox de pagos real (callbacks/confirmaciones). 2. No permitir sobreventa (vender más que el aforo) | Cumple. Valorable para el negocio y testeable en Sandbox |
| **US-03** | Como Cliente, quiero recibir una boleta digital con QR tras confirmar mi pago para ingresar al evento | 1. Generación de QR único por boleta. 2. Envío de notificación por correo/mensaje tras confirmación | Cumple. Pequeña y específica para el flujo de salida. |
| **US-04** | Como Personal de Logística, quiero escanear el QR para validar la entrada de forma rápida | Validación en tiempo real de boleta no usada previamente. 2. Respuesta eficiente incluso con señal de red regular | Cumple. Estimable y crucial para la operación en sitio. |
| **US-05** | Como Gerente, quiero ver métricas de ventas en tiempo real para tomar decisiones comerciales | Dashboard con horas de mayor venta y puntos de caída en el embudo. 2. Reporte de fallas en pagos o ingresos | Cumple. Independiente de la transaccionalidad operativa |
| **US-06** | Como Administrador, quiero un registro de auditoría de cambios en precios y cupos para resolver reclamos | Log inalterable de "quién, cuándo y por qué" se modificó un evento. 2. Trazabilidad completa del ciclo de vida de una orden | Cumple. Esencial para el soporte técnico |

>  **Instrucciones:**  
> - Completar al menos **6 historias de usuario**.  
> - Asegurar que cada historia tenga criterios de aceptación claros y verificables.  

---

## 3. Requisitos de Calidad  
Los requisitos de calidad se presentan en forma de **historias de calidad**, siguiendo la estructura de Len Bass.

### **Historias de Calidad**
| **ID**    | **Fuente**         | **Estímulo**         | **Artefactos**         | **Entorno**         | **Respuesta**         | **Medida de Respuesta**         |
| --------- | ------------------ | -------------------- | ---------------------- | ------------------- | --------------------- | ------------------------------- |
| **RQ-01** | Escalabilidad | Pico de miles de usuarios al abrir ventas | Servicio de Órdenes / DB | Operación normal (lanzamiento) | El sistema procesa las solicitudes sin caerse | Soporte de concurrencia de miles de usuarios por minuto |
| **RQ-02** | Disponibilidad | Falla en el servicio de notificaciones | Microservicio de Notificaciones | Operación continua | El proceso de compra debe continuar aunque falle el aviso | 100% de las ventas finalizan exitosamente a pesar de la caída de servicios externos |
| **RQ-03** | Seguridad | Intento de acceso a funciones de admin por usuario regular | API Gateway / Auth Service | Producción | El sistema bloquea el acceso basado en roles y permisos | 0 accesos no autorizados registrados en auditoría |
| **RQ-04** | Consistencia | Pago en estado "pendiente" o intermedio | Servicio de Pagos | Integración con Pasarela | El sistema espera confirmación (callback) antes de emitir boleta | 0 boletas emitidas sin confirmación real de pago |
| **RQ-05** | Rendimiento | Validación de QR en puerta con baja señal | Servicio de Validación | Evento en vivo | Respuesta rápida para evitar congestión en filas | Tiempo de validación < 2 segundos |
| **RQ-06** | Modificabilidad | Crecimiento de 2 a 10 ciudades | Código fuente / Infraestructura | Evolución del negocio | Se pueden agregar nuevos eventos/ciudades sin rehacer el sistema | Adición de nuevos módulos o servicios sin afectar los existentes |

>  **Instrucciones:**  
> - Completar al menos **6 historias de calidad**, alineadas con atributos clave como **rendimiento, escalabilidad y seguridad**.  
> - Definir cómo se medirá la respuesta esperada ante la situación planteada.  

---

## 4. Restricciones del Sistema  
Las restricciones establecen **limitaciones** en la arquitectura del sistema, ya sean tecnológicas, de negocio, regulatorias o de infraestructura.

### **Lista de Restricciones**
| **Tipo de Restricción** | **Descripción**                                                                                                                                                          |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Tecnológica   | Arquitectura de Microservicios con bases de datos independientes por servicio (no compartir DB) |
| Operativa | Uso de contenedores (Docker) para facilitar el despliegue y las pruebas |
| De Negocio | Entrega del MVP funcional en un plazo máximo de 13 semanas |
| Seguridad] | Prohibido exponer llaves, tokens o secretos en el código fuente |
| Infraestructura | El sistema debe ser levantable y repetible mediante el repositorio (scripts de despliegue) |

---

## 5. Riesgos Identificados

**Riesgos Identificados:**  
- **Concurrencia extrema:** Los picos de ventas pueden saturar la base de datos si no se maneja una cola de espera o caché  
- **Conectividad en sitio:** La validación de QRs depende de la red móvil, la cual es inestable en eventos masivos  
- **Integración de Terceros:** Dependencia de la latencia y disponibilidad de la pasarela de pagos externa 

---

## 6. Arquitectura de Microservicios

> Siguiendo la restricción de no compartir bases de datos y permitir que el sistema crezca por partes, la arquitectura se divide en los siguientes servicios

- **Servicio de Eventos:** Gestiona la creación de eventos, tipos de boletas (General, VIP, Estudiante) y disponibilidad de cupos
- **Servicio de Órdenes:** Maneja el flujo de compra, reserva temporal de inventario y estados de la orden
- **Servicio de Pagos:** Integración con el sandbox de la pasarela real , gestionando callbacks y confirmaciones asíncronas
- **Servicio de Notificaciones:** Envío de correos/mensajes. Si este falla, el proceso de compra no se detiene
- **Servicio de Validación:** Optimizado para consultas rápidas de códigos QR , permitiendo la validación incluso con señal intermitente
- **Servicio de Auditoría:** Registro centralizado de acciones de organizadores y cambios de precios para trazabilidad

---

## 7. Contratos de API (Endpoints Principales)

> Para asegurar que los equipos puedan trabajar en paralelo sin que un servicio "toque la base de datos de otro", definimos los puntos de contacto clave:

**Servicio de Eventos**

- GET /events: Lista eventos disponibles para el público
- POST /events: Creación de evento y definición de tipos de boletas/cupos (Solo Organizador/Admin)
- PATCH /events/{id}/prices: Actualización de precios o cupos con rastro de auditoría


**Servicio de Órdenes y Pagos**

- POST /orders: Inicia el flujo de compra y crea la reserva temporal de inventario para evitar sobreventa
- POST /payments/webhook: Recibe la confirmación asíncrona de la pasarela real (Sandbox) para marcar la orden como pagada
- GET /orders/{id}/status: Permite al cliente ver si su pago fue exitoso, fallido o sigue pendiente

**Servicio de Validación**

- POST /tickets/validate: Recibe el código QR y retorna éxito o error (ej. "ya usado" o "inexistente") de forma eficiente


---

# 8. Plan de Ejecución (MVP en 13 Semanas)

| **Fase**    | **Semanas**         | **Entregables Clave**         | 
| ----------- | --------------------- | ----------------------------- | 
| Cimientos | 1 - 3 | Configuración de microservicios con Docker, API Gateway y Auth (Roles/Permisos) |
| Core de Venta | 4 - 7 | CRUD de Eventos, Lógica de Reserva de Inventario e Integración con Pasarela (Sandbox) |
| Logística y Avisos | 8 - 10 | Generación de QR, Servicio de Notificaciones (Asíncrono) y App de Validación para portería |
| Trazabilidad y Dashboard | 11 - 12 | Implementación de logs de auditoría y métricas en tiempo real para la gerencia |
| Estabilización | 13 | Pruebas de carga (simulando picos de miles de usuarios) y documentación final en el repo |


---

# 9. Estrategia de Resiliencia (Manejo de Fallas)

> Para cumplir con la visión de que "no se caiga todo si una parte falla":

- **Notificaciones en segundo plano:** Si el servicio de correos falla, la venta se completa y el mensaje queda en una cola para salir después
- **Manejo de Pasarela Lenta:** Se implementarán timeouts y estados de "Pago Pendiente" para no bloquear el sistema mientras la pasarela responde
- **Aislamiento de Datos:** Cada microservicio tendrá su propia base de datos (PostgreSQL para transacciones, Redis para reservas rápidas), evitando cuellos de botella compartidos

