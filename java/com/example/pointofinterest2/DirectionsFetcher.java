package com.example.pointofinterest2;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import com.google.api.client.http.GenericUrl;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;


/**
 * Created by Alex on 11/12/2016.
 */

public class DirectionsFetcher extends AsyncTask<String, Integer, String> {

    String message0;
    String message1;
    private ArrayList<LatLng> latLngs;


    public DirectionsFetcher(String message0, String message1){
        this.message0 = message0;
        this.message1 = message1;
        latLngs = new ArrayList<LatLng>();
    }

    public ArrayList<LatLng> getList(){

        return latLngs;
    }

    private int getNodeIndex(NodeList nl, String nodename) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }

    @Override
    protected void onPostExecute(String result) {
        //System.out.println("Fetcher in post execution");
    }


    @Override
    protected String doInBackground(String... params) {

        //Create the url used to get direction data
        GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/directions/xml");
        url.put("origin", message0);
        url.put("destination", message1);
        url.put("key","AIzaSyB3xIjygnDIVqYQKctjdk8uv8U1ZImrBSg");

        String urlStr = url.toString();

        //System.out.println(url);

        try {


            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(urlStr);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
            Document doc = builder.parse(in);

            //output file for testing purposes
            // Use a Transformer for output
            TransformerFactory tFactory =
                    TransformerFactory.newInstance();
            Transformer transformer =
                    tFactory.newTransformer();

            DOMSource source = new DOMSource(doc);

            //File file = new File("C:/Users/Alex/Documents/exportXML.xml");

            /*
            This prints the xml document to the console, used for testing purposes
            StreamResult result = new StreamResult(System.out);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            transformer.transform(source, result);
            */

            //Get directions to destination
            //stored in list of LatLngs

            //parse doc for list of LatLng points
            NodeList nl1, nl2, nl3;
            ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
            nl1 = doc.getElementsByTagName("step");
            if (nl1.getLength() > 0) {
                for (int i = 0; i < nl1.getLength(); i++) {
                    Node node1 = nl1.item(i);
                    nl2 = node1.getChildNodes();

                    Node locationNode = nl2
                            .item(getNodeIndex(nl2, "start_location"));
                    nl3 = locationNode.getChildNodes();
                    Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                    double lat = Double.parseDouble(latNode.getTextContent());
                    Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                    double lng = Double.parseDouble(lngNode.getTextContent());
                    listGeopoints.add(new LatLng(lat, lng));

                    locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
                    nl3 = locationNode.getChildNodes();
                    latNode = nl3.item(getNodeIndex(nl3, "points"));
                    ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
                    for (int j = 0; j < arr.size(); j++) {
                        listGeopoints.add(new LatLng(arr.get(j).latitude, arr
                                .get(j).longitude));
                    }

                    locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                    nl3 = locationNode.getChildNodes();
                    latNode = nl3.item(getNodeIndex(nl3, "lat"));
                    lat = Double.parseDouble(latNode.getTextContent());
                    lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                    lng = Double.parseDouble(lngNode.getTextContent());
                    listGeopoints.add(new LatLng(lat, lng));
                }
            }

            latLngs = listGeopoints;

            //System.out.println("size of LatLng in fetcher: "+ latLngs.size());


            return null;

        }catch (IOException e){
            e.printStackTrace();

            return null;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();

            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (TransformerException e) {
            e.printStackTrace();
            return null;
        }

    }
}
