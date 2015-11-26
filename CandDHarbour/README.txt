La unica variable que se puede modificar es BARCOS_NORM de la clase Puerto ya que el resto de valores son fijos 
conforme al guion de las practicas.

Si se desea probar con multiples ejecuciones se puede configurar la variable MAX_EJECUCIONES de la clase RunTest y ejecutarlo.

La salida por pantalla esta dividida en tres columnas. 
-La primera muestra la entrada y salida de los barcos.
-La segunda muestra como los barcos recargan petroleo y aceite.
-La tercera muestra la parte de descarga de los contenedores.

Los nombres de los paquetes corresponden a cada una de las sesiones que han conformado las clases
de laboratorio, solo hay dos excepciones:

A continuacion se explica como quedan divididas las clases en funcion a las sesiones de 
laboratorio.

Sesion 1 Primitivas - Se usa la clase Barco del paquete barcos. Asi como la clase Puerta del paquete sesionPrimitivas.
Sesion 2 Semaforos - Utiliza la clase BarcoPetrolero y Cargamento del paquete barcos y todas las clases del paquete sesionSemaforos.
Sesion 3 Monitores - Utiliza la clase BarcoMercante del paquete barcos y todas las clases del paquete sesionMonitores.
Sesion 4 Ampliacion en la cual los BarcosPetroleros lanzaban hilos para recargar aceite y petroleo - Usa las clases de la 
	Sesion 2 y las clases de la ampliacionRecargasThread.
Ampliacion Opcional - Recogida en el paquete ampliacionID. Tuvo que modificarse la clase BarcoPetrolero.

Consideraciones de la parte de programacion distribuida:

Para ejecutar el puerto son necesarios tres argumentos, la IP y los nombres de los servicios. Por defecto localhost, ContadorCola
y ContadorMercancia.
En la parte de RMI, en el paquete con el mismo nombre, se debe ejecutar la clase ServidorPuerto. Tiene parametros opcionales pero
no necesarios. Esta configuarda por defecto para que el puerto funcione correctamente con los argumentos anteriormente descritos
(el RunTest funciona de forma similar al puerto).

En los archivos registroPuerta.log y registroMercancia.log queda constancia del resultado de las diferentes ejecuciones.




    .  o ..                    Realizado por:
     o . o o.o                               Guillermo Barco Munioz
          ...oo               	             Fernando Diaz Gonzalez 
            __[]__                           Marcos Fernandez Sellers
         __|_o_o_o\__                        Antonio Manuel Fuentes Duarte
         \""""""""""/         
          \. ..  . /          
     ^^^^^^^^^^^^^^^^^^^^ 