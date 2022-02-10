package com.gt.backend.controller.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.gt.backend.components.JwtTokenUtil;
import com.gt.backend.controller.exceptions.EntityNotFoundException;
import com.gt.backend.dto.ListWrapper;
import com.gt.backend.dto.LoginRequest;
import com.gt.backend.dto.UsuarioDto;
import com.gt.backend.model.sistema.UserRol;
import com.gt.backend.model.sistema.Usuario;
import com.gt.backend.primeng.PageDto;
import com.gt.backend.primeng.Paginator;
import com.gt.backend.service.auth.JwtUserDetailsService;
import com.gt.backend.service.personal.UsuarioService;
import com.gt.tablewriter.XlsxTableWriter;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@CrossOrigin(origins = { "http://localhost:4200" })
@RequestMapping("users")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostMapping("/authenticate")
    public ResponseEntity<UsuarioDto> authenticate(@RequestBody LoginRequest loginRequest) {

        Logger.getLogger(getClass().getName()).log(Level.INFO, "Intentando login para " + loginRequest.getUsername());

        if (usuarioService.getRepo().count() == 0) {
            Logger.getLogger(getClass().getName()).log(Level.INFO,
                    "Sistema sin usuarios, creando usuario administrador con username: " + loginRequest.getUsername());
            Usuario usuario = new Usuario();
            usuario.setCodigo(usuarioService.getRepo().nextCodigo());
            usuario.setNombre("Administrador");
            usuario.setUsername(loginRequest.getUsername());
            usuario.setPassword((new BCryptPasswordEncoder()).encode(loginRequest.getPassword()));
            usuario.getRoles().add(UserRol.ADMIN);
            usuario.getRoles().add(UserRol.USER);
            usuario.setFechaAlta(new Date());
            usuario.setActivo(true);
            usuarioService.getRepo().save(usuario);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (DisabledException e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Usuario deshabilitado");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (BadCredentialsException e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Usuario y contraseña no coinciden");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Usuario> usuario = usuarioService.getRepo().findByUsername(loginRequest.getUsername());

        final UserDetails userDetails;

        if (usuario.isPresent()) {
            userDetails = userDetailsService.loadByUser(usuario.get());
            final String token = jwtTokenUtil.generateToken(userDetails);

            Logger.getLogger(getClass().getName()).log(Level.INFO,
                    "Token generado para " + loginRequest.getUsername() + ": " + token);

            UsuarioDto ret = toDto(usuario.get());

            ret.setToken(token);

            return ResponseEntity.ok(ret);
        } else {
            return ResponseEntity.internalServerError().body(null);
        }

    }

    @Operation(summary = "Obtiene una lista de usuarios", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageDto<UsuarioDto>> getListaUsuarios(@RequestParam(required = false) Paginator paginator)
            throws JsonMappingException, JsonProcessingException {

        PageDto<UsuarioDto> ret = null;

        if (paginator != null) {

            Pageable pageable = paginator.toPageable();

            // Logger.getLogger(getClass().getName()).log(Level.INFO,
            //         "Obteniendo lista de usuarios con paginador " + pageable);

            Page<Usuario> page = usuarioService.findByFilter(paginator.toFiltersMap(), pageable);

            ret = new PageDto<>(paginator.getFirst(), paginator.getRows(), page.getTotalElements(),
                    page.getContent().stream().map(u -> toDto(u)).collect(Collectors.toList()));
        } else {
            // Logger.getLogger(getClass().getName()).log(Level.INFO, "Obteniendo lista de usuarios sin paginador");
            List<UsuarioDto> elements = usuarioService.getRepo().findAll().stream().map(u -> toDto(u))
                    .collect(Collectors.toList());
            ret = new PageDto<>(0, elements.size(), (long) elements.size(), elements);
        }

        return ResponseEntity.ok(ret);

    }

    @Operation(summary = "Obtiene el próximo código de usuario disponible", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/nextCodigo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> getNextCodigo() {

        return ResponseEntity
                .ok(usuarioService.getRepo().nextCodigo());

    }

    @Operation(summary = "Obtiene el usuario buscando por id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{uID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDto> getUsuarioById(@PathVariable Integer uID) throws ParseException {

        Optional<Usuario> oUsuario = usuarioService.getRepo().findById(uID);

        if (oUsuario.isEmpty()) {
            throw new EntityNotFoundException();
        }

        return ResponseEntity.ok(toDto(oUsuario.get()));
    }

    @Operation(summary = "Cambia la contraseña del usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{uID}/setpasswd")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDto> setUserPassword(@PathVariable Integer uID, @RequestBody ListWrapper listWrapper)
            throws ParseException {

        Optional<Usuario> oUsuario = usuarioService.getRepo().findById(uID);

        if (oUsuario.isEmpty()) {
            throw new EntityNotFoundException();
        }

        Usuario usuario = oUsuario.get();

        usuario.setPassword((new BCryptPasswordEncoder()).encode(listWrapper.getList().get(0)));

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Obtiene la lista de roles de un usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{uID}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserRol[]> getUsuarioRoles(@PathVariable Integer uID) throws ParseException {

        Optional<Usuario> oUsuario = usuarioService.getRepo().findById(uID);

        if (oUsuario.isEmpty()) {
            throw new EntityNotFoundException();
        }

        return ResponseEntity.ok(oUsuario.get().getRoles().toArray(new UserRol[] {}));
    }

    @Operation(summary = "Crea un usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDto> createUsuario(@RequestBody UsuarioDto userDto) throws ParseException {

        Usuario usuario = fromDto(null, userDto);

        UsuarioDto ret = toDto(usuarioService.getRepo().save(usuario));

        return ResponseEntity.ok(ret);
    }

    @Operation(summary = "actualiza un usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDto> updateUsuario(@PathVariable("id") Integer id, @RequestBody UsuarioDto userDto)
            throws ParseException {

        Optional<Usuario> oUsuario = usuarioService.getRepo().findById(id);

        if (oUsuario.isEmpty()) {
            throw new EntityNotFoundException();
        }

        Usuario usuario = fromDto(oUsuario.get(), userDto);

        UsuarioDto ret = toDto(usuarioService.getRepo().save(usuario));

        return ResponseEntity.ok(ret);
    }

    @Operation(summary = "Borra un usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> deleteUsuario(@PathVariable("id") Integer id) throws ParseException {

        Optional<Usuario> oUsuario = usuarioService.getRepo().findById(id);

        if (oUsuario.isEmpty()) {
            throw new EntityNotFoundException();
        }

        usuarioService.getRepo().delete(oUsuario.get());

        return ResponseEntity.ok(true);
    }

    @Operation(summary = "Obtiene un excel con la lista de usuarios", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/exportList")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody byte[] getExcelUserList(@RequestParam(required = false) Paginator paginator)
            throws IOException {

        if (paginator != null) {

            paginator.setFirst(0);
            paginator.setRows(Integer.MAX_VALUE);
        }

        XlsxTableWriter writer = new XlsxTableWriter();
        writer.open(new Properties());

        writer.addLine(new String[] { "Id", "Codigo", "Nombre", "Username", "Roles" });

        writer.addLines(getListaUsuarios(paginator).getBody().getElements().stream()
                .map(u -> new Object[] { u.getId(), u.getCodigo(), u.getNombre(), u.getUsername(),
                        Arrays.asList(u.getRoles()).stream().map(UserRol::name).collect(Collectors.joining(", ")) })
                .collect(Collectors.toList()));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writer.writeTo(bos);
        writer.close();

        return bos.toByteArray();
    }

    /*
     * import java.util.Map; import com.gt.backend.util.Utils;
     * 
     * import org.json.simple.JSONArray; import org.json.simple.JSONObject; import
     * org.json.simple.parser.JSONParser;
     * 
     * private Usuario loadFromJsonString(String usuarioJsonString) throws
     * ParseException { return loadFromJsonString(null, usuarioJsonString); }
     * 
     * @SuppressWarnings("unchecked") private Usuario loadFromJsonString(Usuario
     * usuario, String usuarioJsonString) throws ParseException { if (usuario ==
     * null) { usuario = new Usuario(); usuario.setFechaAlta(new Date()); }
     * 
     * JSONParser parser = new JSONParser(); JSONObject json = (JSONObject)
     * parser.parse(usuarioJsonString);
     * 
     * if (json.get("codigo") == null) {
     * usuario.setCodigo(usuarioService.getRepo().nextCodigo()); } else {
     * usuario.setCodigo(((Long) json.get("codigo")).intValue()); }
     * 
     * usuario.setUsername((String) json.get("username")); if (json.get("password")
     * != null) { usuario.setPassword((new BCryptPasswordEncoder()).encode((String)
     * json.get("password"))); } usuario.setNombre((String) json.get("nombre"));
     * usuario.setLegajo((String) json.get("legajo")); usuario.setActivo((Boolean)
     * json.get("activo")); usuario.setObservaciones((String)
     * json.get("observaciones")); usuario.setDocumento((String)
     * json.get("documento"));
     * 
     * if (json.get("fechaAlta") != null) { try {
     * usuario.setFechaAlta(Utils.SDF_DASH_YYMD.parse((String)
     * json.get("fechaAlta"))); } catch (java.text.ParseException e) {
     * Logger.getLogger(getClass().getName()).log(Level.WARNING,
     * "No se puede parsear " + json.get("fechaAlta") + " como fecha yyyy-MM-dd"); }
     * }
     * 
     * if (json.get("vencimientoPass") != null) { try {
     * usuario.setVencimientoPass(Utils.SDF_DASH_YYMD.parse((String)
     * json.get("vencimientoPass"))); } catch (java.text.ParseException e) {
     * Logger.getLogger(getClass().getName()).log(Level.WARNING,
     * "No se puede parsear " + json.get("vencimientoPass") +
     * " como fecha yyyy-MM-dd"); } }
     * 
     * for (Object rol : (JSONArray) json.get("roles")) {
     * usuario.getRoles().add(UserRol.valueOf(rol.toString())); }
     * 
     * usuario.getParametros().clear();
     * 
     * for (Map.Entry<String, String> entry : ((Map<String, String>)
     * json.get("parametros")).entrySet()) {
     * usuario.getParametros().put(entry.getKey().toString(),
     * entry.getValue().toString()); }
     * 
     * usuario.getTelefonos().clear(); for (Object tel : (JSONArray)
     * json.get("telefonos")) { usuario.getTelefonos().add(tel.toString()); }
     * 
     * usuario.getEmails().clear(); for (Object email : (JSONArray)
     * json.get("emails")) { usuario.getEmails().add(email.toString()); }
     * 
     * return usuario; }
     */
    private static UsuarioDto toDto(Usuario u) {
        return new UsuarioDto(u.getId(), u.getCodigo(), u.getNombre(), u.getUsername(),
                u.getRoles().toArray(new UserRol[] {}), null, null);
    }

    private static Usuario fromDto(Usuario usuario, UsuarioDto userDto) {
        if (usuario == null) {
            usuario = new Usuario();
        }
        usuario.setId(userDto.getId());
        usuario.setCodigo(userDto.getCodigo());
        usuario.setNombre(userDto.getNombre());
        usuario.setUsername(userDto.getUsername());
        usuario.getRoles().clear();
        usuario.getRoles().addAll(Arrays.asList(userDto.getRoles()));

        if (userDto.getUnencryptedPassword() != null && !userDto.getUnencryptedPassword().isEmpty()) {
            usuario.setPassword((new BCryptPasswordEncoder()).encode(userDto.getUnencryptedPassword()));
        }

        return usuario;
    }

}
