package com.Car4All.Proyecto.controller;

import com.Car4All.Proyecto.entity.Categoria;
import com.Car4All.Proyecto.entity.Favoritos;
import com.Car4All.Proyecto.entity.dto.AutoDTO;
import com.Car4All.Proyecto.entity.Auto;
import com.Car4All.Proyecto.exception.ResourceNotFoundException;
import com.Car4All.Proyecto.request.ImgUrlAutoRequest;
import com.Car4All.Proyecto.request.UsuarioAutoRequest;
import com.Car4All.Proyecto.service.AutoService;
import com.Car4All.Proyecto.service.CategoriaService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/autos")
public class AutoController {
    private static final Logger logger= LogManager.getLogger(AutoController.class);
    @Autowired
    private AutoService autoService;
    @Autowired
    private CategoriaService categoriaService;
    @PostMapping
    public ResponseEntity<?> registrarAuto(@RequestBody AutoDTO auto) throws ResourceNotFoundException{
        logger.info("Llego la peticion de registrar el auto: "+auto.getModelo());
        return ResponseEntity.ok(autoService.guardarAuto(auto));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizarAuto(@RequestBody AutoDTO auto) throws ResourceNotFoundException {
        logger.info("Llego la peticion de actualizar el auto: "+auto);
        Optional<?> autoBuscado= autoService.buscarPorId(auto.getId());
        if(autoBuscado.isPresent()){
            logger.info("Se actualizo el auto: " + auto.getModelo()+" con el id: "+auto.getId()+".");
            autoService.actualizarAuto(auto);
            return ResponseEntity.ok("Auto: "+auto.getModelo()+" con el id: "+auto.getId()+" actualizado con exito.");
        }else{
            logger.info("No se actualizo el auto: " + auto.getModelo()+" con el id: "+auto.getId()+".");
            throw new ResourceNotFoundException("No se pudo encontrar el auto con el id: "+auto.getId()+".");
        }
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Optional<Auto>> buscarPorId(@PathVariable Integer id) throws ResourceNotFoundException {
        logger.info("Llego la peticion de buscar un auto con el id: "+id);
        Optional<Auto> autoBuscado= autoService.buscarPorId(id);
        if(autoBuscado.isPresent()){
            logger.info("Se encontro el auto con el id: "+id);
            return ResponseEntity.ok(autoBuscado);
        }else{
            logger.info("No se encontro el auto con el id: "+id);
            throw new ResourceNotFoundException("No se pudo encontrar el auto con el id: "+id+".");
        }
    }
    @GetMapping
    public ResponseEntity<List<Auto>> listarAutos() throws ResourceNotFoundException{
        logger.info("Llego la peticion de listar todos los autos");
        List<Auto> listaAutos = autoService.listarAutos();
        if(!listaAutos.isEmpty()){
            logger.info("Existen autos");
            return ResponseEntity.ok(listaAutos);
        }else{
            logger.info("No existen autos");
            throw new ResourceNotFoundException("No hay ningun auto presente en la base de datos.");
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarAuto(@PathVariable Integer id) throws ResourceNotFoundException {
        logger.info("Llego la peticion de eliminar el auto con el id: "+id);
        Optional<Auto> autoBuscado = autoService.buscarPorId(id);
        if(autoBuscado.isPresent()){
            logger.info("Se elimino el auto con el id: "+id);
            autoService.eliminarAuto(id);
            return ResponseEntity.ok("Auto con el id: "+ id +" eliminado con exito.");
        }else
            logger.info("No se elimino el auto con el id: "+id);
        throw  new ResourceNotFoundException("No se encontro auto a eliminar.");
    }
    @PostMapping("/agregar-imagen/porUrl")
    public ResponseEntity<String> agregarImagenAlAuto(@RequestBody ImgUrlAutoRequest request) {
        logger.info("Llego la peticion de agregar una imagen con el url: "+request.getUrl()+" al auto con el id: "+request.getAutoId()+".");
        Optional<Auto> autoOptional = autoService.agregarImagenUrlAlAuto(request.getUrl(), request.getAutoId());
        return autoOptional.map(auto -> ResponseEntity.ok("Imagen agregada al auto"))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Auto o imagen no encontrada"));
    }
    @DeleteMapping("/eliminar-imagen/porUrl")
    public ResponseEntity<String> eliminarImagenAlAuto(@RequestBody ImgUrlAutoRequest request) {
        logger.info("Llegó la petición de eliminar una imagen con la URL: " + request.getUrl() + " al auto con el ID: " + request.getAutoId());
        Optional<Auto> autoOptional = autoService.eliminarImagenUrlAlAuto(request.getUrl(), request.getAutoId());
        if (autoOptional.isPresent()) {
            return ResponseEntity.ok("Imagen eliminada del auto");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Auto o imagen no encontrada");
        }
    }

}
