# Guia Completa de CSS en JavaFX (FXML)

Esta guia te ensena como dar estilo a tus aplicaciones JavaFX usando CSS.
Todos los ejemplos estan basados en este mismo proyecto de Peluqueria.

---

## Indice

1. [Como conectar CSS con FXML](#1-como-conectar-css-con-fxml)
2. [Como aplicar clases a los elementos](#2-como-aplicar-clases-a-los-elementos)
3. [Contenedores (VBox, HBox, GridPane)](#3-contenedores-vbox-hbox-gridpane)
4. [Labels (Etiquetas de texto)](#4-labels-etiquetas-de-texto)
5. [Botones](#5-botones)
6. [Campos de texto (TextField, PasswordField)](#6-campos-de-texto)
7. [ComboBox (Listas desplegables)](#7-combobox)
8. [DatePicker (Selector de fecha)](#8-datepicker)
9. [TableView (Tablas)](#9-tableview)
10. [Layouts y Espaciado](#10-layouts-y-espaciado)
11. [Propiedades CSS mas usadas](#11-propiedades-css-mas-usadas)
12. [Pseudo-clases (hover, focused, etc)](#12-pseudo-clases)
13. [Ejemplos completos del proyecto](#13-ejemplos-completos)

---

## 1. Como conectar CSS con FXML

Para que tu archivo FXML use estilos CSS, agrega el atributo `stylesheets` en el contenedor raiz:

```xml
<VBox stylesheets="@/peluqueria/Css/Login.css">
    <!-- tu contenido aqui -->
</VBox>
```

**Importante:** La `@` al inicio indica que la ruta es relativa al classpath del proyecto.

**Estructura de carpetas recomendada:**
```
src/
  peluqueria/
    Controllers/    <-- Controladores Java
    Vistas/         <-- Archivos FXML
    Css/            <-- Archivos CSS
```

---

## 2. Como aplicar clases a los elementos

En el FXML usas `styleClass` para asignar una clase CSS a cualquier elemento:

```xml
<!-- En el FXML -->
<Label text="Hola Mundo" styleClass="titulo"/>
<Button text="Click" styleClass="btn-accion"/>
```

```css
/* En el CSS */
.titulo {
    -fx-font-size: 22px;
    -fx-font-weight: bold;
}

.btn-accion {
    -fx-background-color: #7C3AED;
    -fx-text-fill: white;
}
```

**Diferencia con CSS web:** En JavaFX TODAS las propiedades llevan el prefijo `-fx-`.

| CSS Web           | CSS JavaFX              |
|-------------------|-------------------------|
| `background-color`| `-fx-background-color`  |
| `font-size`       | `-fx-font-size`         |
| `color`           | `-fx-text-fill`         |
| `border-radius`   | `-fx-border-radius`     |
| `padding`         | `-fx-padding`           |
| `cursor: pointer` | `-fx-cursor: hand`      |

---

## 3. Contenedores (VBox, HBox, GridPane)

Los contenedores son las "cajas" donde metes tus elementos. Son la base de todo layout.

### VBox - Apila elementos VERTICALMENTE (uno debajo del otro)

```xml
<VBox spacing="15" alignment="CENTER" styleClass="mi-caja">
    <Label text="Arriba"/>
    <Label text="En medio"/>
    <Label text="Abajo"/>
</VBox>
```

```
+------------------+
|     Arriba       |
|     En medio     |
|     Abajo        |
+------------------+
```

**Atributos importantes:**
- `spacing="15"` - Espacio entre cada hijo (en pixeles)
- `alignment="CENTER"` - Alinea todo el contenido al centro

**Opciones de alignment:**
```
TOP_LEFT      TOP_CENTER      TOP_RIGHT
CENTER_LEFT   CENTER          CENTER_RIGHT
BOTTOM_LEFT   BOTTOM_CENTER   BOTTOM_RIGHT
```

**CSS para VBox:**
```css
.mi-caja {
    -fx-background-color: white;       /* Color de fondo */
    -fx-background-radius: 12;         /* Esquinas redondeadas */
    -fx-padding: 30;                   /* Espacio interior */
    -fx-spacing: 15;                   /* Espacio entre hijos (igual que spacing del FXML) */
    -fx-alignment: center;             /* Alineacion */
}
```

### HBox - Coloca elementos HORIZONTALMENTE (uno al lado del otro)

```xml
<HBox spacing="15" alignment="CENTER">
    <Label text="Izquierda"/>
    <Label text="Centro"/>
    <Label text="Derecha"/>
</HBox>
```

```
+---------------------------------------+
| Izquierda | Centro | Derecha          |
+---------------------------------------+
```

**Ejemplo real del proyecto (tarjetas del dashboard):**
```xml
<HBox spacing="15">
    <VBox styleClass="card" HBox.hgrow="ALWAYS">
        <Label text="0" styleClass="card-numero"/>
        <Label text="Citas Hoy" styleClass="card-texto"/>
    </VBox>
    <VBox styleClass="card" HBox.hgrow="ALWAYS">
        <Label text="0" styleClass="card-numero"/>
        <Label text="Clientes" styleClass="card-texto"/>
    </VBox>
</HBox>
```

**`HBox.hgrow="ALWAYS"`** = Hace que el elemento se expanda para llenar todo el espacio disponible horizontalmente.
**`VBox.vgrow="ALWAYS"`** = Lo mismo pero verticalmente.

### GridPane - Cuadricula con filas y columnas

Perfecto para formularios donde necesitas campos alineados en columnas.

```xml
<GridPane hgap="15" vgap="12">
    <!-- Definir columnas -->
    <columnConstraints>
        <ColumnConstraints percentWidth="50"/>  <!-- Columna izquierda: 50% -->
        <ColumnConstraints percentWidth="50"/>  <!-- Columna derecha: 50% -->
    </columnConstraints>

    <!-- Fila 0, Columna 0 -->
    <VBox spacing="5" GridPane.columnIndex="0" GridPane.rowIndex="0">
        <Label text="Nombre"/>
        <TextField promptText="Escribe tu nombre"/>
    </VBox>

    <!-- Fila 0, Columna 1 -->
    <VBox spacing="5" GridPane.columnIndex="1" GridPane.rowIndex="0">
        <Label text="Telefono"/>
        <TextField promptText="Escribe tu telefono"/>
    </VBox>

    <!-- Fila 1, Columna 0 -->
    <VBox spacing="5" GridPane.columnIndex="0" GridPane.rowIndex="1">
        <Label text="Email"/>
        <TextField promptText="Escribe tu email"/>
    </VBox>
</GridPane>
```

```
+---------------------+---------------------+
| Nombre              | Telefono            |
| [_________________] | [_________________] |
+---------------------+---------------------+
| Email               |                     |
| [_________________] |                     |
+---------------------+---------------------+
```

**Atributos:**
- `hgap="15"` - Espacio horizontal entre columnas
- `vgap="12"` - Espacio vertical entre filas
- `GridPane.columnIndex="0"` - En que columna va (empieza en 0)
- `GridPane.rowIndex="0"` - En que fila va (empieza en 0)
- `percentWidth="50"` - Porcentaje del ancho para esa columna

---

## 4. Labels (Etiquetas de texto)

Los Labels muestran texto estatico (no editable).

```xml
<Label text="Texto que se muestra" styleClass="mi-label"/>
```

### Estilos comunes para Labels

```css
/* Titulo grande */
.titulo {
    -fx-font-size: 22px;
    -fx-font-weight: bold;
    -fx-text-fill: #1F2937;       /* Color del texto */
}

/* Subtitulo */
.subtitulo {
    -fx-font-size: 16px;
    -fx-font-weight: bold;
    -fx-text-fill: #1F2937;
}

/* Texto pequeno/secundario */
.fecha {
    -fx-font-size: 13px;
    -fx-text-fill: #6B7280;       /* Gris suave */
}

/* Label de formulario */
.label-form {
    -fx-font-size: 12px;
    -fx-font-weight: bold;
    -fx-text-fill: #374151;
}

/* Numero grande (para tarjetas) */
.card-numero {
    -fx-font-size: 28px;
    -fx-font-weight: bold;
    -fx-text-fill: #7C3AED;       /* Morado */
}
```

**Propiedades de texto:**
| Propiedad | Valores | Que hace |
|-----------|---------|----------|
| `-fx-font-size` | `12px`, `16px`, `22px` | Tamano de letra |
| `-fx-font-weight` | `normal`, `bold` | Grosor (normal o negrita) |
| `-fx-text-fill` | `#1F2937`, `white`, `red` | Color del texto |
| `-fx-font-family` | `"Arial"`, `"Segoe UI"` | Tipo de fuente |
| `-fx-font-style` | `normal`, `italic` | Estilo (normal o cursiva) |
| `-fx-text-alignment` | `left`, `center`, `right` | Alineacion del texto |
| `-fx-wrap-text` | `true`, `false` | Si el texto hace salto de linea |

---

## 5. Botones

Los botones son interactivos y tienen estados (normal, hover, presionado).

```xml
<Button text="Agendar Cita" styleClass="btn-accion" onAction="#agendarCita"/>
```

### Boton principal (llamativo)

```css
.btn-accion {
    -fx-background-color: #7C3AED;     /* Fondo morado */
    -fx-text-fill: white;              /* Texto blanco */
    -fx-font-size: 13px;
    -fx-font-weight: bold;
    -fx-padding: 10 24;                /* Espacio interior: 10 arriba/abajo, 24 izq/der */
    -fx-background-radius: 6;          /* Esquinas redondeadas */
    -fx-cursor: hand;                  /* Cursor de manita al pasar el mouse */
}

/* Cuando pasas el mouse encima */
.btn-accion:hover {
    -fx-background-color: #6D28D9;     /* Un morado mas oscuro */
}
```

### Boton secundario (discreto)

```css
.btn-secundario {
    -fx-background-color: transparent;  /* Sin fondo */
    -fx-text-fill: #6B7280;            /* Texto gris */
    -fx-font-size: 13px;
    -fx-padding: 10 20;
    -fx-background-radius: 6;
    -fx-border-color: #D1D5DB;         /* Borde gris */
    -fx-border-radius: 6;
    -fx-cursor: hand;
}

.btn-secundario:hover {
    -fx-background-color: #F3F4F6;     /* Fondo gris muy suave al hover */
}
```

### Boton de sidebar (menu lateral)

```css
.sidebar-btn {
    -fx-background-color: transparent;
    -fx-text-fill: #A0A0C0;
    -fx-font-size: 13px;
    -fx-padding: 10 15;
    -fx-alignment: CENTER_LEFT;        /* Texto alineado a la izquierda */
    -fx-cursor: hand;
    -fx-background-radius: 6;
}

.sidebar-btn:hover {
    -fx-background-color: #2D2D44;
    -fx-text-fill: white;
}

/* Boton activo (pagina actual) */
.sidebar-btn-active {
    -fx-background-color: #7C3AED;
    -fx-text-fill: white;
    -fx-font-weight: bold;
}
```

**Para que un boton ocupe todo el ancho disponible:**
```xml
<Button text="Mi Boton" maxWidth="Infinity"/>
```

---

## 6. Campos de texto

### TextField (campo de texto normal)

```xml
<TextField fx:id="txtNombre" promptText="Ej: Maria Lopez" styleClass="campo-texto"/>
```

### PasswordField (campo de contrasena con puntos)

```xml
<PasswordField fx:id="passwordField" promptText="Ingrese su contrasena" styleClass="campo-texto"/>
```

### CSS para campos de texto

```css
.campo-texto {
    -fx-background-color: #F9FAFB;    /* Fondo gris muy claro */
    -fx-border-color: #D1D5DB;        /* Borde gris */
    -fx-border-radius: 6;             /* Bordes redondeados */
    -fx-background-radius: 6;         /* Fondo con bordes redondeados */
    -fx-padding: 8 12;                /* Espacio interior */
    -fx-font-size: 13px;
}

/* Cuando el campo esta seleccionado (tiene el foco) */
.campo-texto:focused {
    -fx-border-color: #7C3AED;        /* Borde morado */
    -fx-effect: dropshadow(gaussian, rgba(124,58,237,0.15), 4, 0, 0, 0);  /* Sombra morada suave */
}
```

**Importante:** Siempre pon AMBOS `-fx-border-radius` y `-fx-background-radius` con el mismo valor,
si no las esquinas se ven raras.

**`promptText`** es el texto gris que aparece cuando el campo esta vacio (placeholder).

---

## 7. ComboBox

El ComboBox es una lista desplegable para seleccionar una opcion.

```xml
<ComboBox fx:id="cmbServicio"
          promptText="Seleccione un servicio"
          maxWidth="Infinity"
          styleClass="campo-combo"/>
```

```css
.campo-combo {
    -fx-background-color: #F9FAFB;
    -fx-border-color: #D1D5DB;
    -fx-border-radius: 6;
    -fx-background-radius: 6;
    -fx-padding: 4 8;
    -fx-font-size: 13px;
}

.campo-combo:focused {
    -fx-border-color: #7C3AED;
}
```

**`maxWidth="Infinity"`** hace que ocupe todo el ancho disponible de su contenedor padre.

---

## 8. DatePicker

Selector de fecha con calendario desplegable.

```xml
<DatePicker fx:id="dpFecha"
            promptText="Seleccione fecha"
            maxWidth="Infinity"
            styleClass="campo-fecha"/>
```

```css
.campo-fecha {
    -fx-background-color: #F9FAFB;
    -fx-border-color: #D1D5DB;
    -fx-border-radius: 6;
    -fx-background-radius: 6;
    -fx-font-size: 13px;
}

.campo-fecha:focused {
    -fx-border-color: #7C3AED;
}
```

---

## 9. TableView

Las tablas muestran datos en filas y columnas.

### FXML

```xml
<TableView fx:id="tablaCitas" styleClass="tabla-citas" VBox.vgrow="ALWAYS">
    <columns>
        <TableColumn fx:id="colCliente" text="Cliente" prefWidth="140"/>
        <TableColumn fx:id="colServicio" text="Servicio" prefWidth="130"/>
        <TableColumn fx:id="colFecha" text="Fecha" prefWidth="100"/>
    </columns>
</TableView>
```

- `VBox.vgrow="ALWAYS"` = La tabla se expande verticalmente para llenar el espacio
- `prefWidth="140"` = Ancho preferido de cada columna en pixeles

### CSS

```css
/* Contenedor de la tabla */
.tabla-citas {
    -fx-background-color: white;
    -fx-background-radius: 8;
    -fx-border-radius: 8;
    -fx-border-color: #E5E7EB;
}

/* Fondo de los headers (titulos de columna) */
.tabla-citas .column-header-background {
    -fx-background-color: #F9FAFB;
}

/* Texto de los headers */
.tabla-citas .column-header .label {
    -fx-font-weight: bold;
    -fx-font-size: 12px;
    -fx-text-fill: #374151;
    -fx-padding: 8 10;
}

/* Cada celda de la tabla */
.tabla-citas .table-cell {
    -fx-font-size: 12px;
    -fx-text-fill: #4B5563;
    -fx-padding: 6 10;
}

/* Filas impares (para efecto zebra) */
.tabla-citas .table-row-cell:odd {
    -fx-background-color: #F9FAFB;
}

/* Fila seleccionada */
.tabla-citas .table-row-cell:selected {
    -fx-background-color: #EDE9FE;
}
```

**Nota:** Para estilizar partes internas de la tabla se usan selectores descendientes
(con espacio), como `.tabla-citas .table-cell`. Esto es porque la tabla
tiene subelementos internos que JavaFX genera automaticamente.

---

## 10. Layouts y Espaciado

### Padding (espacio INTERIOR)

```xml
<!-- Opcion 1: En el FXML con etiqueta -->
<VBox>
    <padding>
        <Insets top="20" bottom="20" left="10" right="10"/>
    </padding>
</VBox>
```

```css
/* Opcion 2: En CSS */
.mi-caja {
    -fx-padding: 30;              /* 30px en todos los lados */
    -fx-padding: 10 20;           /* 10 arriba/abajo, 20 izq/der */
    -fx-padding: 10 20 15 20;     /* arriba, derecha, abajo, izquierda */
}
```

### Spacing (espacio ENTRE hijos)

```xml
<VBox spacing="15">   <!-- 15px entre cada elemento hijo -->
```

### Region (espaciador invisible)

Region es un elemento invisible que empuja otros elementos. Muy util para crear espacio flexible.

```xml
<!-- Empuja el boton "Cerrar Sesion" hasta el fondo del sidebar -->
<VBox>
    <Button text="Inicio"/>
    <Button text="Citas"/>

    <Region VBox.vgrow="ALWAYS"/>    <!-- Este se expande y empuja lo de abajo -->

    <Button text="Cerrar Sesion"/>
</VBox>
```

```
+------------------+
|  Inicio          |
|  Citas           |
|                  |  <-- Region invisible llenando el espacio
|                  |
|                  |
|  Cerrar Sesion   |
+------------------+
```

```xml
<!-- Empuja los botones a la derecha en un HBox -->
<HBox>
    <Label text="Mensaje"/>
    <Region HBox.hgrow="ALWAYS"/>   <!-- Empuja a la derecha -->
    <Button text="Cancelar"/>
    <Button text="Guardar"/>
</HBox>
```

```
+-----------------------------------------------+
| Mensaje                  | Cancelar | Guardar  |
+-----------------------------------------------+
```

---

## 11. Propiedades CSS mas usadas

### Fondos

```css
/* Color solido */
-fx-background-color: #7C3AED;
-fx-background-color: white;
-fx-background-color: transparent;           /* Sin fondo */

/* Degradado (gradiente) */
-fx-background-color: linear-gradient(to bottom, #667eea, #764ba2);

/* Esquinas redondeadas del fondo */
-fx-background-radius: 8;
-fx-background-radius: 8 8 0 0;             /* Solo arriba redondeado */
```

### Bordes

```css
-fx-border-color: #D1D5DB;                  /* Color del borde */
-fx-border-width: 1;                        /* Grosor del borde */
-fx-border-radius: 6;                       /* Esquinas del borde redondeadas */
-fx-border-style: solid;                    /* Tipo: solid, dashed, dotted */
```

### Sombras

```css
/* dropshadow(tipo, color, radio, spread, offsetX, offsetY) */
-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);

/* Sombra morada suave (para focus de inputs) */
-fx-effect: dropshadow(gaussian, rgba(124,58,237,0.15), 4, 0, 0, 0);
```

**Parametros de dropshadow:**
1. `gaussian` - Tipo de desenfoque (siempre usa gaussian)
2. `rgba(0,0,0,0.08)` - Color con transparencia
3. `6` - Radio del desenfoque
4. `0` - Spread (expansion)
5. `0` - Desplazamiento horizontal
6. `2` - Desplazamiento vertical

### Texto

```css
-fx-text-fill: white;                       /* Color del texto en Labels/Buttons */
-fx-fill: white;                            /* Color del texto en Text */
-fx-font-size: 16px;
-fx-font-weight: bold;                      /* normal o bold */
-fx-font-family: "Segoe UI";
```

**Cuidado:** Labels y Buttons usan `-fx-text-fill`. El elemento `Text` usa `-fx-fill`.

### Cursor

```css
-fx-cursor: hand;                           /* Manita (para botones) */
-fx-cursor: default;                        /* Flecha normal */
```

---

## 12. Pseudo-clases

Las pseudo-clases cambian el estilo segun el ESTADO del elemento.

```css
/* Cuando el mouse esta encima */
.btn-accion:hover {
    -fx-background-color: #6D28D9;
}

/* Cuando el elemento esta clickeado */
.btn-accion:pressed {
    -fx-background-color: #5B21B6;
}

/* Cuando un campo de texto tiene el foco (esta seleccionado) */
.campo-texto:focused {
    -fx-border-color: #7C3AED;
}

/* Cuando un elemento esta deshabilitado */
.btn-accion:disabled {
    -fx-opacity: 0.5;
}

/* Filas impares de una tabla */
.tabla-citas .table-row-cell:odd {
    -fx-background-color: #F9FAFB;
}

/* Fila seleccionada */
.tabla-citas .table-row-cell:selected {
    -fx-background-color: #EDE9FE;
}
```

---

## 13. Ejemplos completos

### Ejemplo 1: Tarjeta (Card) con sombra

```xml
<!-- FXML -->
<VBox styleClass="card">
    <Label text="42" styleClass="card-numero"/>
    <Label text="Citas Hoy" styleClass="card-texto"/>
</VBox>
```

```css
/* CSS */
.card {
    -fx-background-color: white;
    -fx-background-radius: 8;
    -fx-padding: 20;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);
}

.card-numero {
    -fx-font-size: 28px;
    -fx-font-weight: bold;
    -fx-text-fill: #7C3AED;
}

.card-texto {
    -fx-font-size: 13px;
    -fx-text-fill: #6B7280;
}
```

### Ejemplo 2: Formulario con label + campo

```xml
<!-- FXML -->
<VBox spacing="5">
    <Label text="Nombre del Cliente" styleClass="label-form"/>
    <TextField promptText="Ej: Maria Lopez" styleClass="campo-texto"/>
</VBox>
```

```css
/* CSS */
.label-form {
    -fx-font-size: 12px;
    -fx-font-weight: bold;
    -fx-text-fill: #374151;
}

.campo-texto {
    -fx-background-color: #F9FAFB;
    -fx-border-color: #D1D5DB;
    -fx-border-radius: 6;
    -fx-background-radius: 6;
    -fx-padding: 8 12;
    -fx-font-size: 13px;
}

.campo-texto:focused {
    -fx-border-color: #7C3AED;
}
```

### Ejemplo 3: Layout con sidebar + contenido

```xml
<!-- FXML -->
<HBox>
    <!-- Sidebar a la izquierda -->
    <VBox prefWidth="200" styleClass="sidebar" spacing="5">
        <padding>
            <Insets top="20" bottom="20" left="10" right="10"/>
        </padding>
        <Label text="MI APP" styleClass="sidebar-title"/>
        <Button text="Inicio" styleClass="sidebar-btn-active" maxWidth="Infinity"/>
        <Button text="Pagina 2" styleClass="sidebar-btn" maxWidth="Infinity"/>

        <Region VBox.vgrow="ALWAYS"/>   <!-- Empuja logout abajo -->

        <Button text="Cerrar Sesion" styleClass="sidebar-btn-logout" maxWidth="Infinity"/>
    </VBox>

    <!-- Contenido a la derecha (se expande) -->
    <VBox styleClass="contenido" spacing="20" HBox.hgrow="ALWAYS">
        <padding>
            <Insets top="25" right="25" bottom="25" left="25"/>
        </padding>
        <Label text="Bienvenido" styleClass="titulo"/>
        <!-- Mas contenido aqui -->
    </VBox>
</HBox>
```

```css
/* CSS */
.sidebar {
    -fx-background-color: #1E1E2E;
}

.sidebar-title {
    -fx-text-fill: white;
    -fx-font-size: 18px;
    -fx-font-weight: bold;
}

.sidebar-btn {
    -fx-background-color: transparent;
    -fx-text-fill: #A0A0C0;
    -fx-padding: 10 15;
    -fx-alignment: CENTER_LEFT;
    -fx-cursor: hand;
    -fx-background-radius: 6;
}

.sidebar-btn:hover {
    -fx-background-color: #2D2D44;
    -fx-text-fill: white;
}

.sidebar-btn-active {
    -fx-background-color: #7C3AED;
    -fx-text-fill: white;
    -fx-font-weight: bold;
}

.contenido {
    -fx-background-color: #F5F5FA;
}
```

---

## Tips y Errores Comunes

1. **Siempre usa `-fx-` antes de cada propiedad.** Es el error mas comun.
   - MAL: `background-color: red;`
   - BIEN: `-fx-background-color: red;`

2. **Para bordes redondeados, pon AMBOS:**
   ```css
   -fx-border-radius: 6;
   -fx-background-radius: 6;
   ```
   Si solo pones uno, el fondo y el borde no coinciden.

3. **El color de texto depende del elemento:**
   - `Label`, `Button` --> `-fx-text-fill`
   - `Text` --> `-fx-fill`

4. **Para ocultar el fondo gris por defecto de un boton:**
   ```css
   -fx-background-color: transparent;
   ```

5. **Los imports en FXML son obligatorios.** Si usas un `VBox`, necesitas:
   ```xml
   <?import javafx.scene.layout.VBox?>
   ```

6. **`maxWidth="Infinity"`** es tu amigo para que botones y campos ocupen todo el ancho.

7. **Colores en formato hex:** Siempre con `#` y 6 digitos.
   ```css
   -fx-background-color: #7C3AED;    /* Morado */
   -fx-background-color: #1F2937;    /* Gris oscuro */
   -fx-background-color: #F9FAFB;    /* Gris muy claro */
   ```

8. **Para colores con transparencia** usa `rgba()`:
   ```css
   rgba(0, 0, 0, 0.08)       /* Negro al 8% de opacidad */
   rgba(124, 58, 237, 0.15)  /* Morado al 15% */
   ```
