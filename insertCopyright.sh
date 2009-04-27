#! /bin/bash
fileType=$1;
fileCopyright=$2;
echo "Fichiers type a ajouter le copyright $fileType "
echo "Fichier du copyright $fileCopyright"
dirF=`find . -name $fileType`
echo "------------"
for dir in $dirF
do
   echo "Concatenation du copyrigth dans $dir.bis "
   (cat $fileCopyright; echo; cat $dir) >$dir."bis"
   echo "Ajout effectue"
   echo "Suppression du fichier d'origine $dir"
   rm $dir
   echo "Renommage du fichier concatene $dir.bis en $dir"
   mv $dir."bis" $dir
done
echo "--------------"
echo "fin"
