function [ salida ] = Fuzzy2( imagen, archivo )
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here

            %imagen = 'Captura10.PNG';
            %archivo = 'Captura3.txt';

            % CARGAMOS LAS REDES Y LAS RENOMBRAMOS A CONVENIENCIA:
            load(strcat('Redes/Original/Camara/Hue/WS_Completo.mat'));
            redHUE = red;
            %clearvars -except imagen estrategia serie n_estado tasa_aciertos  dispositivo dispositivos estado estados redHUE;
            load(strcat('Redes/Original/Camara/Value/WS_Completo.mat'));
            redVAL = red;
            %clearvars -except imagen estrategia serie n_estado tasa_aciertos  dispositivo dispositivos estado estados redHUE redVAL;
            load(strcat('Redes/Original/Camara/Chroma/WS_Completo.mat'));
            redCHR = red;
            %clearvars -except estrategia serie n_estado tasa_aciertos  dispositivo dispositivos estado estados redHUE redVAL redCHR;

            f = imread(imagen);
            %f = WhitePatch(f,0);
            %f = GrayWorld(f);
            R = mode(f(:,:,1));
            G = mode(f(:,:,2));
            B = mode(f(:,:,3));
            RGB=[R(1,2);G(1,2);B(1,2)];
            
            % CALCULAMOS LAS SALIDAS DE LAS REDES. PRIMERO LA DEL HUE, QUE SERÁ ENTRADA
            % DE LAS OTRAS DOS:
            F_HUE = redHUE(RGB);
            
            % CONSTRUIMOS LA ENTRADA INCLUYENDO EL HUE PARA LAS OTRAS DOS REDES:
            RGBH = [RGB;F_HUE];
            
            F_VAL = redVAL(RGBH);
            F_CHR = redCHR(RGBH);
            
            % CONSTRUIMOS LAS ENTRADAS DEL FUZZY CON LOS RESULTADOS OBTENIDOS POR LAS
            % REDES. CONSTRUIMOS TAMBIEN LAS SALIDAS CON LOS VALORES REALES PARA
            % COMPROBAR QUE EL SISTEMA DIFUSO FUNCIONA CORRECTAMENTE:
            F_input = [F_HUE',F_VAL',F_CHR'];
            
            % CARGAMOS EL SISTEMA FUZZY
            fuzzy_system = readfis('Selector Chip Munshell.fis');      
       
            % CALCULAMOS LA SALIDA DEL FUZZY PARA LOS RESULTADOS DE LAS REDES
            [salida IRR_actual ORR_actual ARR_actual] = evalfis(F_input(1,1:3), fuzzy_system);
            
            
            fileID = fopen(archivo,'w');
            fprintf(fileID,'%6.2f\n',ARR_actual);
            fclose(fileID);
end

