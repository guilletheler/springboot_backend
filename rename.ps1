$fileNames = Get-ChildItem $args[0] -File -Recurse | Select-Object -expand fullname

foreach ($filename in $filenames) 
{
  (  Get-Content $fileName) -replace $args[1], $args[2] | Set-Content $fileName
}
